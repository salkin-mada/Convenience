ConvenientCatalog {
    classvar <synthsBuild = false;
	classvar <fxsynthsBuild = false;
	classvar <addingSynths = false;

	classvar <classpath, <coremodules, <fxmodules, <fxsynthdefs, <fxlist;

	*addSynths { | server |

		classpath = Main.packages.asDict.at('Convenience');
		coremodules = IdentityDictionary.new;

		if (addingSynths.asBoolean.not,{
		addingSynths = true;

		server.doWhenBooted{
			/* var win = Window.new("adding synths", Rect(450,450,250,250), resizable: false)
			.background_(Color.green)
			.alwaysOnTop_(true)
			.front; */

			"Convenience is talking to %".format(server).postln;
			"building synth definitions".postln;

				{ // forked on AppClock
				var folder = classpath +/+ "Modules/Core/";
				folder.postln;
				// load Core modules
				PathName(folder).filesDo{|file|
					var ext = file.extension;
					var name = file.fileNameWithoutExtension.asSymbol;
					file.postln;
					if(ext == "scd", {
						var contents;
						"loading coremodule file: %".format(name).postln;
						file.fullPath.load;
						server.sync;
					});
				};

				//win.close;
				
				synthsBuild = true;
				addingSynths = false;

				"Convenience synths build".postln;

				//^synthsBuild;
				}.fork(AppClock)
			}
		});
	}

	*addFxs { | numChannels = 2 |
		classpath = Main.packages.asDict.at('Convenience');
		fxmodules = IdentityDictionary.new;
		fxlist=IdentityDictionary.new;

		this.prLoadFxModulesToDict(numChannels);
		this.prMakeFxSynthDefs(numChannels);
		this.prMakeFxList();

		fxsynthsBuild = true;
	}

	*prLoadFxModulesToDict{ | numChannels |
		var folder = classpath +/+ "Modules/Fx/";

		// Load Fx Modules
		PathName(folder).filesDo{ | file |
			var ext = file.extension;
			var name = file.fileNameWithoutExtension.asSymbol;

			if(ext == "scd", {
				var contents;
				contents = file.fullPath.load.value(numChannels);
				fxmodules.put(name, contents)
			})
		}

		//^fxmodules
	}

	*getFx{ | name |
		^fxlist[name]
	}

	*prMakeFxList{
		// Category
		fxmodules.keysValuesDo{|category, content|
			// Modules in category
			content.keysValuesDo{|moduleName, moduleContent|
				fxlist.put(moduleName, moduleContent)
			}
		};

		//^fxlist
	}

	*prMakeFxSynthDefs{ | numChannels = 2 |

		// Category
		fxmodules.keysValuesDo{|category, content|

			// Modules in category
			content.keysValuesDo{|moduleName, moduleContent|
				var fxdef, fxdefname;
				fxdefname = moduleName.asString ++ numChannels;

				//"loading module % from category % ".format(moduleName, category).poststamped;

				// Create synthdef
				fxdef = SynthDef(fxdefname.asSymbol, { | in, out, wet=1.0 |
					var insig = In.ar(in, numChannels);
					var sig = SynthDef.wrap(moduleContent, prependArgs: [insig]);

					XOut.ar(out, wet, sig);
				}).add;

				// Add to global fx synthdef array of instance
				fxsynthdefs = fxsynthdefs.add(fxdef);

				//"SynthDef added: SynthDef('%')".format(defname).poststamped;

			}
		}
	}
}

// These following Ugens is used with the biggest thanks to it originators
// They are used here as these renamed copies for Convenient purposes

ConvenientBufferPlayer {
	// copy of PlayBufCF
	*ar { arg numChannels, bufnum=0, rate=1.0, trigger=1.0, startPos=0.0, loop = 0.0,
		lag = 0.1, n = 2; // alternative for safemode

		var index, method = \ar, on;

		switch ( trigger.rate,
			\audio, {
				index = Stepper.ar( trigger, 0, 0, n-1 );
			},
			\control, {
				index = Stepper.kr( trigger, 0, 0, n-1 );
				method = \kr;
			},
			\demand, {
				trigger = TDuty.ar( trigger ); // audio rate precision for demand ugens
				index = Stepper.ar( trigger, 0, 0, n-1 );
			},
			{ ^PlayBuf.ar( numChannels, bufnum, rate, trigger, startPos, loop ); } // bypass
		);

		on = n.collect({ |i|
			//on = (index >= i) * (index <= i); // more optimized way?
			InRange.perform( method, index, i-0.5, i+0.5 );
		});

		switch ( rate.rate,
			\demand,  {
				rate = on.collect({ |on, i|
					Demand.perform( method, on, 0, rate );
				});
			},
			\control, {
				rate = on.collect({ |on, i|
					Gate.kr( rate, on ); // hold rate at crossfade
				});
			},
			\audio, {
				rate = on.collect({ |on, i|
					Gate.ar( rate, on );
				});
			},
			{
				rate = rate.asCollection;
			}
		);

		if( startPos.rate == \demand ) {
			startPos = Demand.perform( method, trigger, 0, startPos )
		};

		lag = 1/lag.asArray.wrapExtend(2);

		^Mix(
			on.collect({ |on, i|
				PlayBuf.ar( numChannels, bufnum, rate.wrapAt(i), on, startPos, loop )
				* Slew.perform( method, on, lag[0], lag[1] ).sqrt
			})
		);

	}
}

// copy of PitchShiftPA.sc
// https://github.com/dyfer/PitchShiftPA/blob/master/Classes/PitchShiftPA.sc
// PitchShiftPA is based on formant preserving pitch-synchronous overlap-add re-synthesis, as developed by Keith Lent
// based on real-time implementation by Juan Pampin, combined with non-real-time implementation by Joseph Anderson
// pseudo-UGen by Marcin Pączkowski, using GrainBuf and a circular buffer
ConvenientPitchShiftPA {
    *ar { arg in, freq = 440, pitchRatio = 1, formantRatio = 1, minFreq = 10, maxFormantRatio = 10, grainsPeriod = 2;

        var out, localbuf, grainDur, wavePeriod, trigger, freqPhase, maxdelaytime, grainFreq, bufSize, delayWritePhase, grainPos;
        var absolutelyMinValue = 0.01; // used to ensure positive values before reciprocating
        var numChannels = 1;

        //multichanel expansion
        [in, freq, pitchRatio, formantRatio].do({ arg item;
            item.isKindOf(Collection).if({ numChannels = max(numChannels, item.size) });
        });

        in = in.asArray.wrapExtend(numChannels);
        freq = freq.asArray.wrapExtend(numChannels);
        pitchRatio = pitchRatio.asArray.wrapExtend(numChannels);

        minFreq = minFreq.max(absolutelyMinValue);
        maxdelaytime = minFreq.reciprocal;

        freq = freq.max(minFreq);

        wavePeriod = freq.reciprocal;
        grainDur = grainsPeriod * wavePeriod;
        grainFreq = freq * pitchRatio;

        if(formantRatio.notNil, { //regular version

            formantRatio = formantRatio.asArray.wrapExtend(numChannels);

            maxFormantRatio = maxFormantRatio.max(absolutelyMinValue);
            formantRatio = formantRatio.clip(maxFormantRatio.reciprocal, maxFormantRatio);

            bufSize = ((SampleRate.ir * maxdelaytime * maxFormantRatio) + (SampleRate.ir * ControlDur.ir)).roundUp; //extra padding for maximum delay time
            freqPhase = LFSaw.ar(freq, 1).range(0, wavePeriod) + ((formantRatio.max(1) - 1) * grainDur);//phasor offset for formant shift up - in seconds; positive here since phasor is subtracted from the delayWritePhase

        }, { //slightly lighter version, without formant manipulation

            formantRatio = 1 ! numChannels;

            bufSize = ((SampleRate.ir * maxdelaytime) + (SampleRate.ir * ControlDur.ir)).roundUp; //extra padding for maximum delay time
            freqPhase = LFSaw.ar(freq, 1).range(0, wavePeriod);
        });

        localbuf = numChannels.collect({LocalBuf(bufSize, 1).clear});
        delayWritePhase = numChannels.collect({|ch| BufWr.ar(in[ch], localbuf[ch], Phasor.ar(0, 1, 0, BufFrames.kr(localbuf[ch])))});
        grainPos = (delayWritePhase / BufFrames.kr(localbuf)) - (freqPhase / BufDur.kr(localbuf)); //scaled to 0-1 for use in GrainBuf
        trigger = Impulse.ar(grainFreq);
        out = numChannels.collect({|ch| GrainBuf.ar(1, trigger[ch], grainDur[ch], localbuf[ch], formantRatio[ch], grainPos[ch])});

        ^out;
    }
}