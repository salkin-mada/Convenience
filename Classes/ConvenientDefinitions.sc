ConvenientDefinitions {
    classvar <synthsBuild = false;

	*addSynths { | server |

		var win = Window.new("adding synths", Rect(450,450,250,250))
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
			attack = 0.01, decay = 0.5, sustain = 0.5, release = 1.0, pos = 0,
			pattack = 0.0, pdecay = 0.0, psustain = 1.0, prelease = 100.0,
			// long hack.. when penv is not in use, penv should always be longer than env
			gate = 1, cutoff = 22e3, bass = 0.0
			|
			var sig, key, frames, env, penv, file;
			penv = EnvGen.ar(Env.adsr(pattack, pdecay, psustain, prelease), gate);
			frames = BufFrames.kr(bufnum);
			sig = ConvenientBufferPlayer.ar(
				1,
				bufnum,
				rate*BufRateScale.kr(bufnum)*penv,
				1,
				pos*frames,
				loop: loop
			);
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate);
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
			attack = 0.01, decay = 0.5, sustain = 0.5, release = 1.0, pos = 0,
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
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate);
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
			attack = 0.01, decay = 0.5, sustain = 0.5, release = 1.0, pos = 0,
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
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate);
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
			attack = 0.01, decay = 0.5, sustain = 0.5, release = 1.0, pos = 0,
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
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate);
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
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate, doneAction: 2);
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
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate, doneAction: 2);
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
		env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate, doneAction: 2);
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
			attack = 0.01, decay = 0.5, sustain = 0.5, release = 1.0, pos = 0,
			pattack = 0.0, pdecay = 0.0, psustain = 1.0, prelease = 100.0,
			// prelease long hack.. when "penvelope" is not in use, "penvelope" should always be longer than amplitude envelope
			gate = 1, cutoff = 22e3, bass = 0.0, pitchRatio = 1.0, formantRatio = 1.0
			|
			var sig, key, frames, env, penv, file;
			penv = EnvGen.ar(Env.adsr(pattack, pdecay, psustain, prelease), gate);
			frames = BufFrames.kr(bufnum);
			sig = ConvenientBufferPlayer.ar(
				1,
				bufnum,
				rate*BufRateScale.kr(bufnum)*penv,
				1,
				pos*frames,
				loop: loop
			);
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate);
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
			attack = 0.01, decay = 0.5, sustain = 0.5, release = 1.0, pos = 0,
			gate = 1, cutoff = 22e3, bass = 0.0, basefreq=440, freq,
            pattack = 0.0, pdecay = 0.0, psustain = 1.0, prelease = 100.0,
			// prelease long hack.. when "penvelope" is not in use, "penvelope" should always be longer than amplitude envelope
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
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate);
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

        "Convenience synths build".postln;

		synthsBuild = true;
		//^synthsBuild;
		}.fork(AppClock)
	}
}

// These following Ugens is used with the biggest thanks to it originators
// They are used here as these renamed copies for Convenience purposes

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