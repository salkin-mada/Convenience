Salk {
	classvar <dir, <buffers;
	classvar makeBuffersFn;

	const <supportedExtensions = #[\wav, \WAV, \wave, \aif, \aiff, \flac];

	*initClass {
		buffers = Dictionary.new;
		makeBuffersFn = #{ |server| Salk.prMakeBuffers(server) };
		this.prAddEventType;
	}

	*p { | name, type=\Salk, out = 0, folder, index = 1, dur = 8, stretch = 1.0,
		pos = 0, loop = 0, rate = 1, degree = 0, octave = 3, root = 0, scale, cutoff = 22e3, bass = 0,
		pan = 0, spread = 0, amp = 0.5, attack = 0.1, decay = 0.5,
		sustain=1.0, release = 0.5, tempo = 120, tuningOnOff = 0, basefreq = 440, fftOnOff = 0, binRange = 20 |

		//var return;

		if(name.isNil,{"needs a key aka name, please".throw; ^nil});

		if(folder.isNil, {
			if(Salk.folders.asArray[0].isNil.not, {
				folder = Salk.folders.asArray[0]
			}, {"not init corr no folder avai".throw; ^nil})
		});
		if(scale.isNil, {
			scale = Scale.chromatic;
		});

		Pdef(name,
			Pbind(
				\type, type,
				\fftOnOff, fftOnOff,
				\tuningOnOff, tuningOnOff,
				\basefreq, basefreq,
				\out, out,
				\folder, folder,
				\index, index,
				\dur, dur,
				\stretch, stretch,
				\pos, pos,
				\loop, loop,
				\rate, rate,
				\degree, degree,
				\octave, octave,
				\root, root,
				\scale, scale,
				\cutoff, cutoff,
				\bass, bass,
				\pan, pan,
				\spread, spread,
				\amp, amp,
				\attack, attack,
				\decay, decay,
				\sustain, sustain,
				\release, release,
				\binRange, binRange
			);
		).play(TempoClock(tempo/60*4));

		//return = 123.rand;
		//^return;
	}

	*s { | name |
		Pdef(name).stop
	}

	*loadFolders { |path, server|
		dir = path;
		if (dir.isNil) { Error("this is not a directory").throw };

		server = server ? Server.default;

		// create buffers on boot
		ServerBoot.add(makeBuffersFn, server);

		// if server is running create rightaway
		if (server.serverRunning) {
			this.prMakeBuffers(server);
		};

		this.prAddSynthDefinitions;
		"Salk synths build".postln;
	}

	*free { |server|
		this.prFreeBuffers;
		server = server ? Server.default;
		ServerBoot.remove(makeBuffersFn, server);
		"files freed".postln;
	}

	*get { |folder, index|
		if (buffers.isNil.not) {
			var bufList = buffers[folder.asSymbol];
			if (bufList.isNil.not) {
				index = index % bufList.size;
				^bufList[index]
			}
		};
		^nil
	}

	*folders {
		^buffers.keys
	}

	*folderNum { |index|
		var folder;

		if (buffers.isNil.not) {

			^buffers.keys.asArray[index.wrap(0,buffers.keys.size-1)]
		}

		/*if (buffers.isNil.not) {
			var folder, bufList;
			folder =
			bufList = buffers[folder.asSymbol];

			if (bufList.isNil.not) {
				index = index % bufList.size;
				"index er % bufList size er %".format(index, bufList.size).postln;
				^bufList[index]
			}
		};*/
		^nil

	}

	*files {
		^buffers.do { |folderName, buffers|
			"% %".format(folderName, buffers.size).postln
		}
	}

	*list {
		^buffers.keysValuesDo { |folderName, buffers|
			"% [%]".format(folderName, buffers.size).postln
		}
	}

	*prFreeBuffers {
		buffers.do { |folders|
			folders.do { |buf|
				if (buf.isNil.not) {
					buf.free
				}
			}
		};
		buffers.clear;
	}

	*prMakeBuffers { | server |
		this.prFreeBuffers;

		PathName(dir).entries.do { | subfolder |
			var entries;
			entries = subfolder.entries.select { | entry |
				supportedExtensions.includes(entry.extension.asSymbol)
			};
			// monofy entry, no stereo
			entries = entries.collect { | entry |
				Buffer.readChannel(server, entry.fullPath, channels: [0])
			};
			if (entries.isEmpty.not) {
				buffers.add(subfolder.folderName.asSymbol -> entries)
			}
		};

		"% folders loaded".format(buffers.size).postln;
	}

	*prAddSynthDefinitions {
		/*	  --------------------------------------  */
		/*	  rate style							 */
		/*	  ------------------------------------- */
		SynthDef(\SalkMono, {
			|
			bufnum, out = 0, loop = 0, rate = 1, spread = 1, pan = 0, amp = 0.5,
			attack = 0.01, decay = 0.5, sustain = 0.5, release = 1.0, pos = 0,
			gate = 1, cutoff = 22e3, bass = 0.0
			|
			var sig, key, frames, env, file;
			frames = BufFrames.kr(bufnum);
			sig = SalkBufferPlay.ar(
				1,
				bufnum,
				rate*BufRateScale.kr(bufnum),
				1,
				pos*frames,
				loop: loop
			);
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate);
			FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
			sig = LPF.ar(sig, cutoff);
			sig = sig + (LPF.ar(sig, 100, bass));
			sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
			Out.ar(out, (sig*env));
		}).add;

		SynthDef(\SalkStereo, {
			|
			bufnum, out = 0, loop = 0, rate = 1, spread = 1, pan = 0, amp = 0.5,
			attack = 0.01, decay = 0.5, sustain = 0.5, release = 1.0, pos = 0,
			gate = 1, cutoff = 22e3, bass = 0.0
			|
			var sig, key, frames, env, file;
			frames = BufFrames.kr(bufnum);
			sig = SalkBufferPlay.ar(
				2,
				bufnum,
				rate*BufRateScale.kr(bufnum),
				1,
				pos*frames,
				loop: loop
			);
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate);
			FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
			sig = LPF.ar(sig, cutoff);
			sig = sig + (LPF.ar(sig, 100, bass));
			sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
			Out.ar(out, (sig*env));
		}).add;

		/*	  --------------------------------------  */
		/*	  for scaling, assuming samples are tuned */
		/*	  ------------------------------------- */
		SynthDef(\SalkMonoScale, {
			|
			bufnum, out = 0, loop = 0, spread = 1, pan = 0, amp = 0.5,
			attack = 0.01, decay = 0.5, sustain = 0.5, release = 1.0, pos = 0,
			gate = 1, cutoff = 22e3, bass = 0.0, basefreq=440, freq
			|
			var sig, rate, frames, env, file;
			frames = BufFrames.kr(bufnum);
			rate = freq/basefreq;
			sig = SalkBufferPlay.ar(
				1,
				bufnum,
				rate*BufRateScale.kr(bufnum),
				1,
				pos*frames,
				loop: loop
			);
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate);
			FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
			sig = LPF.ar(sig, cutoff);
			sig = sig + (LPF.ar(sig, 100, bass));
			sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
			Out.ar(out, (sig*env));
		}).add;

		SynthDef(\SalkStereoScale, {
			|
			bufnum, out = 0, loop = 0, spread = 1, pan = 0, amp = 0.5,
			attack = 0.01, decay = 0.5, sustain = 0.5, release = 1.0, pos = 0,
			gate = 1, cutoff = 22e3, bass = 0.0, basefreq=440, freq
			|
			var sig, rate, frames, env, file;
			frames = BufFrames.kr(bufnum);
			rate = freq/basefreq;
			sig = SalkBufferPlay.ar(
				2,
				bufnum,
				rate*BufRateScale.kr(bufnum),
				1,
				pos*frames,
				loop: loop
			);
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate);
			FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
			sig = LPF.ar(sig, cutoff);
			sig = sig + (LPF.ar(sig, 100, bass));
			sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
			Out.ar(out, (sig*env));
		}).add;

		/*	  --------------------------------------  */
		/*	  bfft filter bins				synth	 */
		/*	  ------------------------------------- */
		~frame = 1024;
		SynthDef(\SalkBufBins, { | bufnum, out = 0, win = 1, loop = 0, spread = 1, pan = 0, amp = 0.5,
		binRange =#[0, 512], gate = 1, attack = 0.01, decay = 0.01, sustain = 2,
		release = 0.01, pos = 0, rate = 1 |
			var in, chain, env, frames, sig;
			frames = BufFrames.kr(bufnum);
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate, doneAction: 2);
			in = PlayBuf.ar(1, bufnum, rate * BufRateScale.kr(bufnum), startPos: pos * frames, loop: loop);
			chain = FFT(LocalBuf(~frame), in);
			chain = chain.pvcollect(~frame, {| mag, phase, index |
				if(index >= binRange[0], if(index <= binRange[1], mag, 0), 0);
			}, frombin: 0, tobin: (~frame / 2) - 1, zeroothers: 0);
			sig = IFFT(chain, win) * amp * env;
			sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
			Out.ar(out, sig);
		}).add;

		SynthDef(\SalkBufBinsScale, { | bufnum, out = 0, win = 1, loop = 0, spread = 1, pan = 0, amp = 0.5,
		binRange =#[0, 512], gate = 1, attack = 0.01, decay = 0.01, sustain = 2,
		release = 0.01, pos = 0, basefreq=440, freq |
			var in, chain, env, frames, rate, sig;
			frames = BufFrames.kr(bufnum);
			rate = freq/basefreq;
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate, doneAction: 2);
			in = PlayBuf.ar(1, bufnum, rate * BufRateScale.kr(bufnum), startPos: pos * frames, loop: 0);
			chain = FFT(LocalBuf(~frame), in);
			chain = chain.pvcollect(~frame, {| mag, phase, index |
				if(index >= binRange[0], if(index <= binRange[1], mag, 0), 0);
			}, frombin: 0, tobin: (~frame / 2) - 1, zeroothers: 0);
			sig = IFFT(chain, win) * amp * env;
			sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
			Out.ar(out, sig);
		}).add;

		/*	  --------------------------------------  */
		/*	  bfft filter bins input		synth	 */
		/*	  ------------------------------------- */
		/*SynthDef(\SalkInBins, { | out = 0, in = 0, win = 1, amp = 0.5,
		binRange =#[0, 512], gate = 1, attack = 0.01, decay = 0.01, sustain = 2,
		release = 0.01, rate = 1 |
			var sig, chain, env;
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate, doneAction: 2);
			sig = SoundIn.ar(in);
			chain = FFT(LocalBuf(~frame), sig);
			chain = chain.pvcollect(~frame, {| mag, phase, index |
				if(index >= binRange[0], if(index <= binRange[1], mag, 0), 0);
			}, frombin: 0, tobin: (~frame / 2) - 1, zeroothers: 0);
			Out.ar(out, IFFT(chain, win) * amp * env);
		}).add;*/

	}

	*prAddEventType {
		Event.addEventType(\Salk, {
			var numChannels, scaling, fft;

			if (~buf.isNil) {
				var folder = ~folder;
				if (folder.isNil.not) {
					var index = ~index ? 0;
					~buf = Salk.get(folder, index)
				} {
					var sample = ~sample;
					if (sample.isNil.not) {
						var pair, folder, index;
						pair = sample.split($:);
						folder = pair[0].asSymbol;
						index = if (pair.size == 2) { pair[1].asInt } { 0 };
						~buf = Salk.get(folder, index)
					}
				}
			};

			numChannels = ~buf.bufnum.numChannels;

			scaling = ~tuningOnOff;
			if(scaling.isNil) {scaling = 0};
			fft = ~fftOnOff;
			if(fft.isNil) {fft = 0};

			case
			{fft == 1 and: scaling == 0} {
				~instrument = \SalkBufBins;
				//"FFT".postln;
			}
			{fft == 1 and: scaling == 1} {
				~instrument = \SalkBufBinsScale;
				//"FFT+SCALING".postln;
			}
			{scaling == 1 and: fft == 0} {
				//"SCALING -- ".post;
				switch(numChannels,
					1, {
						~instrument = \SalkMonoScale;
						//"mono".postln;
					},
					2, {
						~instrument = \SalkStereoScale;
						//"stereo".postln;
					},
					{
						~instrument = \SalkMonoScale;
						//"mono-default".postln;
					}
				);

			}
			{
				//"NORMALES -- ".post;
				switch(numChannels,
					1, {
						~instrument = \SalkMono;
						//"mono".postln;
					},
					2, {
						~instrument = \SalkStereo;
						//"stereo".postln;
					},
					{
						~instrument = \SalkMono;
						//"mono-default".postln;
					}
				);
			};

			~type = \note;
			~bufnum = ~buf.bufnum;
			currentEnvironment.play
		});
	}

	*buckets {| frame = 1024, numBands = 4, band = 0 |
		var result, coeff, binLow, binHigh;

		//snap frame to power of 2
		frame = 2**frame.log2.round;

		//numBands should not be lower than 1
		if(numBands < 1, {numBands = 1}, {numBands});

		//selected band index clipped into sensible range
		band = band.clip(0, numBands -1);

		//the coefficient for calculating logarithmically scaled bands
		//(the same equation as used for equal temperament)
		coeff = (frame / 2)**(1 / numBands);

		//calculate the "bin-range" of the selected band
		binLow = ((coeff**band).round - 1).clip(0, (frame / 2) - 1);
		if(band == (numBands - 1), {
			binHigh = ((coeff**(band + 1)).round - 1).clip(binLow, (frame / 2) - 1)
		}, {binHigh = ((coeff**(band + 1)).round - 2).clip(binLow, (frame / 2) - 1)
		});
		result = [binLow, binHigh];
		//return the bin-range in an array
		^result
	}


}


SalkBufferPlay {
	// stolen PlayBufCF
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