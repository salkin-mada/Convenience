ConvenientCatalog {
    classvar <synthsBuild = false;
	classvar <addingSynths = false;
    
	classvar <modules;
    classvar <functions;

    classvar <>numLFOs;
    classvar lfosLoaded = false;

	*addSynths { | server |

		if (addingSynths.asBoolean.not,{
		addingSynths = true;

		server.doWhenBooted{
			var win = Window.new("adding synths", Rect(450,450,250,250), resizable: false)
			.background_(Color.green)
			.alwaysOnTop_(true)
			.front;
			// var buildFeed = Routine({
			// 	loop{
			// 		"\n".post;
			// 		11.do{
			// 			".".post;
			// 			0.1.wait;
			// 		};
			// 		//0.1.wait;
			// 	};
			// });

			"Convenience is talking to %".format(server).postln;

				{ // fork it
				"building synth definitions".postln;
				//buildFeed.play;

				/*	  --------------------------------------  */
				/*	  rate style							 */
				/*	  ------------------------------------- */
				SynthDef(\ConvenienceMono, {
					|
					bufnum, out = 0, loop = 0, rate = 1, spread = 1, pan = 0, amp = 0.5,
					attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
					gate = 1, cutoff = 22e3, bass = 0.0
					|
					var sig, key, frames, env, file;
					frames = BufFrames.kr(bufnum);
					sig = ConvenientBufferPlayer.ar(
						1,
						bufnum,
						rate*BufRateScale.kr(bufnum),
						1,
						pos*frames,
						loop: loop
					);
					env = EnvGen.ar(Env.linen(attack, sustain, release), gate);
					FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
					sig = LPF.ar(sig, cutoff);
					sig = sig + (LPF.ar(sig, 100, bass));
					sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
					sig = LeakDC.ar(sig);
					Out.ar(out, (sig*env));
				}).add;

				server.sync;
				
				SynthDef(\ConvenienceStereo, {
					|
					bufnum, out = 0, loop = 0, rate = 1, spread = 1, pan = 0, amp = 0.5,
					attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
					gate = 1, cutoff = 22e3, bass = 0.0
					|
					var sig, key, frames, env, file;
					frames = BufFrames.kr(bufnum);
					sig = ConvenientBufferPlayer.ar(
						2,
						bufnum,
						rate*BufRateScale.kr(bufnum),
						1,
						pos*frames,
						loop: loop
					);
					env = EnvGen.ar(Env.linen(attack, sustain, release), gate);
					FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
					sig = LPF.ar(sig, cutoff);
					sig = sig + (LPF.ar(sig, 100, bass));
					sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
					sig = LeakDC.ar(sig);
					Out.ar(out, (sig*env));
				}).add;

				server.sync;
				
				/*	  --------------------------------------  */
				/*	  for scaling, assuming samples are tuned */
				/*	  ------------------------------------- */
				SynthDef(\ConvenienceMonoScale, {
					|
					bufnum, out = 0, loop = 0, spread = 1, pan = 0, amp = 0.5,
					attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
					gate = 1, cutoff = 22e3, bass = 0.0, basefreq=440, freq
					|
					var sig, rate, frames, env, file;
					frames = BufFrames.kr(bufnum);
					rate = freq/basefreq;
					sig = ConvenientBufferPlayer.ar(
						1,
						bufnum,
						rate*BufRateScale.kr(bufnum),
						1,
						pos*frames,
						loop: loop
					);
					env = EnvGen.ar(Env.linen(attack, sustain, release), gate);
					FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
					sig = LPF.ar(sig, cutoff);
					sig = sig + (LPF.ar(sig, 100, bass));
					sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
					sig = LeakDC.ar(sig);
					Out.ar(out, (sig*env));
				}).add;
				
				server.sync;

				SynthDef(\ConvenienceStereoScale, {
					|
					bufnum, out = 0, loop = 0, spread = 1, pan = 0, amp = 0.5,
					attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
					gate = 1, cutoff = 22e3, bass = 0.0, basefreq=440, freq
					|
					var sig, rate, frames, env, file;
					frames = BufFrames.kr(bufnum);
					rate = freq/basefreq;
					sig = ConvenientBufferPlayer.ar(
						2,
						bufnum,
						rate*BufRateScale.kr(bufnum),
						1,
						pos*frames,
						loop: loop
					);
					env = EnvGen.ar(Env.linen(attack, sustain, release), gate);
					FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
					sig = LPF.ar(sig, cutoff);
					sig = sig + (LPF.ar(sig, 100, bass));
					sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
					sig = LeakDC.ar(sig);
					Out.ar(out, (sig*env));
				}).add;
				
				server.sync;

				/*	  --------------------------------------  */
				/*	  bfft filter bins				synth	 */
				/*	  ------------------------------------- */
				~frame = 1024;
				SynthDef(\ConvenienceBufBins, { | bufnum, out = 0, win = 1, loop = 0, spread = 1, pan = 0, amp = 0.5,
					binRange =#[0, 512], gate = 1, attack = 0.01, decay = 0.01, sustain = 2,
					release = 0.01, pos = 0, rate = 1 |
					var in, chain, env, frames, sig;
					frames = BufFrames.kr(bufnum);
					env = EnvGen.ar(Env.linen(attack, sustain, release), gate, doneAction: 2);
					in = PlayBuf.ar(1, bufnum, rate * BufRateScale.kr(bufnum), startPos: pos * frames, loop: loop);
					chain = FFT(LocalBuf(~frame), in);
					chain = chain.pvcollect(~frame, {| mag, phase, index |
						if(index >= binRange[0], if(index <= binRange[1], mag, 0), 0);
					}, frombin: 0, tobin: (~frame / 2) - 1, zeroothers: 0);
					sig = IFFT(chain, win) * amp * env;
					sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
					sig = LeakDC.ar(sig);
					Out.ar(out, sig);
				}).add;

				server.sync;
				
				SynthDef(\ConvenienceBufBinsScale, { | bufnum, out = 0, win = 1, loop = 0, spread = 1, pan = 0, amp = 0.5,
					binRange =#[0, 512], gate = 1, attack = 0.01, decay = 0.01, sustain = 2,
					release = 0.01, pos = 0, basefreq=440, freq |
					var in, chain, env, frames, rate, sig;
					frames = BufFrames.kr(bufnum);
					rate = freq/basefreq;
					env = EnvGen.ar(Env.linen(attack, sustain, release), gate, doneAction: 2);
					in = PlayBuf.ar(1, bufnum, rate * BufRateScale.kr(bufnum), startPos: pos * frames, loop: 0);
					chain = FFT(LocalBuf(~frame), in);
					chain = chain.pvcollect(~frame, {| mag, phase, index |
						if(index >= binRange[0], if(index <= binRange[1], mag, 0), 0);
					}, frombin: 0, tobin: (~frame / 2) - 1, zeroothers: 0);
					sig = IFFT(chain, win) * amp * env;
					sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
					sig = LeakDC.ar(sig);
					Out.ar(out, sig);
				}).add;

				server.sync;
				
				/*	  --------------------------------------  */
				/*	  bfft filter bins input		synth	 */
				/*	  ------------------------------------- */
				/*SynthDef(\ConvenienceInBins, { | out = 0, in = 0, win = 1, amp = 0.5,
				binRange =#[0, 512], gate = 1, attack = 0.01, decay = 0.01, sustain = 2,
				release = 0.01, rate = 1 |
				var sig, chain, env;
				env = EnvGen.ar(Env.linen(attack, sustain, release), gate, doneAction: 2);
				sig = SoundIn.ar(in);
				chain = FFT(LocalBuf(~frame), sig);
				chain = chain.pvcollect(~frame, {| mag, phase, index |
				if(index >= binRange[0], if(index <= binRange[1], mag, 0), 0);
				}, frombin: 0, tobin: (~frame / 2) - 1, zeroothers: 0);
				Out.ar(out, IFFT(chain, win) * amp * env);
				}).add;*/        

				/*	  --------------------------------------  */
				/*	  PitchShift            		synth	 */
				/*	  ------------------------------------- */
				
				SynthDef(\ConveniencePitchShift, {
					|
					bufnum, out = 0, loop = 0, rate = 1, spread = 1, pan = 0, amp = 0.5,
					attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
					gate = 1, cutoff = 22e3, bass = 0.0, pitchRatio = 1.0, formantRatio = 1.0
					|
					var sig, key, frames, env, file;
					frames = BufFrames.kr(bufnum);
					sig = ConvenientBufferPlayer.ar(
						1,
						bufnum,
						rate*BufRateScale.kr(bufnum),
						1,
						pos*frames,
						loop: loop
					);
					env = EnvGen.ar(Env.linen(attack, sustain, release), gate);
					FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
					sig = ConvenientPitchShiftPA.ar(
						sig,
						Pitch.kr(sig)[0], //pitch tracking
						pitchRatio,
						formantRatio
					);
					sig = LPF.ar(sig, cutoff);
					sig = sig + (LPF.ar(sig, 100, bass));
					sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
					sig = LeakDC.ar(sig);
					Out.ar(out, (sig*env));
					
				}).add;

				server.sync;

				SynthDef(\ConveniencePitchShiftScale, {
					|
					bufnum, out = 0, loop = 0, spread = 1, pan = 0, amp = 0.5,
					attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
					gate = 1, cutoff = 22e3, bass = 0.0, basefreq=440, freq,
					pitchRatio = 1.0, formantRatio = 1.0
					|
					var sig, rate, frames, env, file;
					frames = BufFrames.kr(bufnum);
					rate = freq/basefreq;
					sig = ConvenientBufferPlayer.ar(
						1,
						bufnum,
						rate*BufRateScale.kr(bufnum),
						1,
						pos*frames,
						loop: loop
					);
					env = EnvGen.ar(Env.linen(attack, sustain, release), gate);
					FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
					sig = ConvenientPitchShiftPA.ar(
						sig,
						Pitch.kr(sig)[0], //pitch tracking
						pitchRatio,
						formantRatio
					);
					sig = LPF.ar(sig, cutoff);
					sig = sig + (LPF.ar(sig, 100, bass));
					sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
					sig = LeakDC.ar(sig);
					Out.ar(out, (sig*env));
					
				}).add;

				server.sync;

				//buildFeed.stop;
				win.close;
				
				synthsBuild = true;
				addingSynths = false;

				"Convenience synths build".postln;

				//^synthsBuild;
				}.fork(AppClock)
			}
		});
	}

/* 	*prModules { | numchans = 2 |
		modules = (
			comb: {|in, delay=0.25, decay=1|
				CombC.ar(
					in, 
					0.5, 
					delay.linlin(-1.0,1.0,0.0001,5), 
					decay.linlin(-1.0,1.0,0.01,2.0)
				)
			},
			allpass: {|in, delay=0.25, decay=1|
				AllpassC.ar(
					in, 
					0.5, 
					delay.linlin(-1.0,1.0,0.0001,5.0), 
					decay.linlin(-1.0,1.0,0.01,5.0)
				)
			},
			hpf: {|in, cutoff=500, res=0.75|
				DFM1.ar(
					in, 
					cutoff.linexp(-1.0,1.0,40,20000), 
					res.linlin(-1.0,1.0,0.0,1.0), 
					1, 
					1, 
					0
				)

			},
			lpf: {|in, cutoff=500, res=0.75|
				DFM1.ar(
					in, 
					cutoff.linexp(-1.0,1.0,40,20000), 
					res.linlin(-1.0,1.0,0.0,1.0), 
					1, 
					0, 
					0
				)

			},
			// Ring modulation stolen from SuperDirt. ring=modilation amount, ringf=modfreq,ringdf=slide in modfreq
			dirtring: { |in, ringf = 0.5, ringdf=0.15|
				var signal, mod;
				ringf = ringf.linlin(-1.0,1.0,0.0001,1.0);
				signal = in;
				mod = SinOsc.ar(XLine.kr(ringf, ringf + ringdf.linlin(-1.0,1.0,0.001,1.0)).linexp(0.0,1.0,20,20000));
				ring1(signal, mod)/2;
			},
			// Taken from Thor Magnussons book Scoring Sound: https://leanpub.com/ScoringSound/read#leanpub-auto-flanger 
			flanger: { |in, flangdelay=0.1, flangdepth=0.08, flangrate=0.06, flangfb=0.01|
				var input, maxdelay, maxrate, dsig, mixed, local;
				maxdelay = 0.013;
				maxrate = 10.0;
				input = in;
				flangdelay = flangdelay.linlin(-1.0,1.0,0.0001,0.1);
				flangrate = flangrate.linlin(-1.0,1.0,0.00001,1.0);
				flangdepth = flangdepth.linlin(-1.0,1.0,0.0,1.0);
				flangfb = flangfb.linlin(-1.0,1.0,0.0,1.0);

				local = LocalIn.ar(numchans, 0.0);

				dsig = AllpassC.ar( 
					input + (local * flangfb),
					maxdelay * 2,
					// very similar to SinOsc (try to replace it) - Even use LFTri
					LFPar.kr( 
						flangrate * maxrate,
						0,
						flangdepth * maxdelay,
						flangdelay * maxdelay
					),
					0
				);

				mixed = input + dsig;
				LocalOut.ar(mixed);
				mixed;
			}, 

			// Taken from Thor Magnussons book Scoring Sound: https://leanpub.com/ScoringSound/read#leanpub-auto-chorus 
			chorus: { |in, chpredelay=0.08, chrate=0.05, chdepth=0.1, chphasediff=0.5|
					var sig, modulators, numDelays = 12;
					chpredelay = chpredelay.linlin(-1.0,1.0,0.0001,0.25);
					chrate = chrate.linexp(-1.0,1.0,0.0001,10.25);
					chdepth = chdepth.linlin(-1.0,1.0,0.0,1.0);
					chphasediff = chphasediff.linlin(-1.0,1.0,0.00001,1.0);

					in = in * numDelays.reciprocal;
					modulators = Array.fill(numDelays, {arg i;
						LFPar.kr(chrate * rrand(0.94, 1.06), chphasediff * i, chdepth, chpredelay)}
					); 
					sig = DelayC.ar(in, 0.5, modulators);  
					numDelays.reciprocal * sig.sum!numchans;
				},
			waveloss: {|in, loss=0.25|
				WaveLoss.ar(in, loss.linlin(-1.0,1.0,0,40),  outof: 40,  mode: 2)
			},
			fs: {|in, shiftfreq=1|
				FreqShift.ar(in, shiftfreq)
			},
			ps: {|in, pitch=1.25, pd=0.001, td=0.0001|
				PitchShift.ar(
					in, 
					0.25, 
					pitch.linlin(-1.0,1.0,0.0,5.0), 
					pd.linlin(-1.0,1.0,0.0,1.0), 
					td.linlin(-1.0,1.0,0.0,1.0)
				)
			},
			freeverb: {|in, verb=0.5, damp=0.25|
				FreeVerb2.ar(
					in[0],  
					in[1],  
					1,  
					verb.linexp(-1.0,1.0,0.001,5.0),  
					damp.linlin(-1.0,1.0,0.0,1.0)
				)
			},
		);
	}

    *prFunctions {
        // Control functions
		functions = (
			saw: {|freq=1, amp=1|
				LFSaw.kr(LFDNoise3.kr(freq*10) * freq) * amp
			},
			lfnoise3: {|freq=1, amp=1|
				LFDNoise3.kr(freq, amp)
			},
			fbsaw1: {|freq=0.5, amp=1.0|
				var fb = LocalIn.kr(1,0.5).lag;
				var sig = LFSaw.kr(freq+fb, 0);

				sig = sig + LFSaw.kr(sig + freq / 2,  pi);
				sig = sig + LFSaw.kr(sig + freq / 3,  -pi);
				sig = sig + VarSaw.kr(sig + freq * 3.3, 0, sig * fb.unipolar.lag3,  mul: amp);

				sig = sig * LFNoise2.kr(freq * 100).range(0.90,1.1);

				sig = sig.wrap(-1.0,1.0);

				sig = sig.lag2;

				LocalOut.kr(sig);

				sig
			},
			sinoscfb: {|freq=1, amp=1|
				SinOscFB.kr(freq, LFNoise2.kr(freq*11).unipolar, amp)
			},
			henon1: {|freq=10, amp=1|
				A2K.kr(
					HenonC.ar(
						freq, 
						LFNoise2.kr(freq, 0.2, 1.5),
						LFNoise2.kr(freq*10, 0.5, 0.15),
					) * amp
				)
			},
			noisering: {|freq=1.0001, amp=1.0|
				Demand.kr(
					Impulse.kr(freq*10), 
					0, 
					DNoiseRing(
						change: LFNoise2.kr(freq*10).unipolar,					
						chance: LFNoise2.kr(freq*100).unipolar,					
						numBits: 32
					)
				).linlin(0, 2**32, -1.0, 1.0) * amp
			}
		);
    }

    *prSynthGraph { | name, busnum = 0, numLayers = 10, out |

		if(out.isNil.not, {
			// prepare module
			this.prModules;

			// Make synth graph
			name = name.asSymbol;
			Ndef(name).clear;

			Ndef(name, {
				SoundIn.ar(busnum)
			}).playN(out);

			(1..numLayers).do{ | i | 
				Ndef(name)[i] = \kfilter -> modules.choose
				// Ndef(name)[i] = \kfilter -> e[\comb]
			};

			Ndef(name)[1000] = \filter -> { | in |
				LeakDC.ar(Limiter.ar(in/2))
			};

		}, {"module needs an output destination, please".postln})

    }

    *prMakeLFOs { |freq, randomSeed = 9123|
		//prepare functions
		this.prFunctions;

        thisThread.randSeed_(randomSeed);

        (1..numLFOs).do{|i|
            var name = "lfo%".format(i).asSymbol;
            Ndef(name).source = functions.choose; // get random func
            // lfo freq multiplied per lfo queried by synthGraph name
            Ndef(name).set(\freq, exprand(0.01,freq * i), \amp, rrand(0.0,1.0))
        };

        lfosLoaded = true;
    }

    *prClearLFOs {

    }

    *prMapLFOs { | target, prob |

        if(Ndef(target).isPlaying, {
                
            if(lfosLoaded == true, {
                Ndef(target).controlNames.do{ | ctrl |
                    var name = ctrl.name;

                    if(prob.coin, {
                        var lfoname = "lfo%".format(numLFOs.rand).asSymbol;

                        "mapping % to %".format(lfoname, name).postln;

                        Ndef(target).map(
                            name, 
                            Ndef(lfoname)
                        ).set(\wet1000, 1)
                    }, {
                        "not mapping %".format(name).postln;
                    })
                }
            })       
        }, {"target does not exist".postln})
    } */
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