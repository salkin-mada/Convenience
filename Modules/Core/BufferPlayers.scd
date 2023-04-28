/*	  --------------------------------------  */
/*	  rate style							 */
/*	  ------------------------------------- */
Convenience.maxChannelArraySize.do{arg channels;
    var freeSelfDelay=0.01;
    channels = channels + 1;

    "Convenience:: building % channel output synths".format(channels).postln;

    SynthDef(("ConvenienceMono_"++channels).asSymbol, {
        |
        bufnum, out, loop = 0, rate = 1, pan = 0, spread = 0.5, width = 2, amp = 0.5,
        attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
        /*gate = 1,*/ cutoff = 22e3, res = 0.01, fgain = 1.0, ftype = 0, bass = 0.0
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
        env = EnvGen.ar(Env.linen(attack, sustain, release), /*gate*/);
        FreeSelf.kr(TDelay.kr(Done.kr(env),freeSelfDelay));
        sig = DFM1.ar(in: sig, freq: cutoff, res: res, inputgain: fgain, type: ftype);
        sig = sig + (LPF.ar(sig, 100, bass));
        // sig = SplayAz.ar(channels, sig, spread: spread, width: width, center: pan, level: amp);
        sig = Pan2.ar(in:sig, pos:pan, level:amp);
        sig = LeakDC.ar(sig);
        Out.ar(out, (sig*env));
    }).add;

    SynthDef(("ConvenienceStereo_"++channels).asSymbol, {
        |
        bufnum, out, loop = 0, rate = 1, pan = 0, spread = 0.5, width = 2, amp = 0.5,
        attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
        /*gate = 1,*/ cutoff = 22e3, res = 0.01, fgain = 1.0, ftype = 0, bass = 0.0
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
        env = EnvGen.ar(Env.linen(attack, sustain, release), /*gate*/);
        FreeSelf.kr(TDelay.kr(Done.kr(env),freeSelfDelay));
        sig = DFM1.ar(in: sig, freq: cutoff, res: res, inputgain: fgain, type: ftype);
        sig = sig + (LPF.ar(sig, 100, bass));
        sig = SplayAz.ar(channels, sig, spread: spread, width: width, center: pan, level: amp);
        sig = LeakDC.ar(sig);
        Out.ar(out, (sig*env));
    }).add;

    /*	  --------------------------------------  */
    /*	           for scaling                   */
    /*	  ------------------------------------- */
    SynthDef(("ConvenienceMonoScale_"++channels).asSymbol, {
        |
        bufnum, out, loop = 0, pan = 0, spread = 0.5, width = 2, amp = 0.5,
        attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
        /*gate = 1,*/ cutoff = 22e3, res = 0.01, fgain = 1.0, ftype = 0, bass = 0.0, basefreq=440, freq
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
        env = EnvGen.ar(Env.linen(attack, sustain, release), /*gate*/);
        FreeSelf.kr(TDelay.kr(Done.kr(env),freeSelfDelay));
        sig = DFM1.ar(in: sig, freq: cutoff, res: res, inputgain: fgain, type: ftype);
        sig = sig + (LPF.ar(sig, 100, bass));
        // sig = SplayAz.ar(channels, sig, spread: spread, width: width, center: pan, level: amp);
        sig = Pan2.ar(in:sig, pos:pan, level:amp);
        sig = LeakDC.ar(sig);
        Out.ar(out, (sig*env));
    }).add;

    SynthDef(("ConvenienceStereoScale_"++channels).asSymbol, {
        |
        bufnum, out, loop = 0, pan = 0, spread = 0.5, width = 2, amp = 0.5,
        attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
        /*gate = 1,*/ cutoff = 22e3, res = 0.01, fgain = 1.0, ftype = 0, bass = 0.0, basefreq=440, freq
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
        env = EnvGen.ar(Env.linen(attack, sustain, release), /*gate*/);
        FreeSelf.kr(TDelay.kr(Done.kr(env),freeSelfDelay));
        sig = DFM1.ar(in: sig, freq: cutoff, res: res, inputgain: fgain, type: ftype);
        sig = sig + (LPF.ar(sig, 100, bass));
        sig = SplayAz.ar(channels, sig, spread: spread, width: width, center: pan, level: amp);
        sig = LeakDC.ar(sig);
        Out.ar(out, (sig*env));
    }).add;

    /*	  --------------------------------------  */
    /*	  bfft filter bins				synth	 */
    /*	  ------------------------------------- */
    /* ~frame = 1024;
    SynthDef(("ConvenienceBufBins_"++channels).asSymbol, { | bufnum, out = 0, win = 1, loop = 0, pan = 0, spread = 0.5, width = 2, amp = 0.5,
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
        sig = SplayAz.ar(channels, sig, spread: spread, center: pan, level: amp);
        sig = LeakDC.ar(sig);
        Out.ar(out, sig);
    }).add;

    SynthDef(("ConvenienceBufBinsScale_"++channels).asSymbol, { | bufnum, out = 0, win = 1, loop = 0, pan = 0, spread = 0.5, width = 2, amp = 0.5,
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
        sig = SplayAz.ar(channels, sig, spread: spread, center: pan, level: amp);
        sig = LeakDC.ar(sig);
        Out.ar(out, sig);
    }).add; */

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

    SynthDef(("ConvenienceMonoPitchShift_"++channels).asSymbol, {
        |
        bufnum, out, loop = 0, rate = 1, pan = 0, spread = 0.5, width = 2, amp = 0.5,
        attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
        /*gate = 1,*/ cutoff = 22e3, res = 0.01, fgain = 1.0, ftype = 0, bass = 0.0, pr = 1.0, fr = 1.0
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
        env = EnvGen.ar(Env.linen(attack, sustain, release), /*gate*/);
        FreeSelf.kr(TDelay.kr(Done.kr(env),freeSelfDelay));
        sig = ConvenientPitchShiftPA.ar(
            sig,
            Pitch.kr(sig)[0], //pitch tracking
            pr,
            fr
        );
        sig = DFM1.ar(in: sig, freq: cutoff, res: res, inputgain: fgain, type: ftype);
        sig = sig + (LPF.ar(sig, 100, bass));
        // sig = SplayAz.ar(channels, sig, spread: spread, width: width, center: pan, level: amp);
        sig = Pan2.ar(in:sig, pos:pan, level:amp);
        sig = LeakDC.ar(sig);
        Out.ar(out, (sig*env));
    }).add;

    SynthDef(("ConvenienceStereoPitchShift_"++channels).asSymbol, {
        |
        bufnum, out, loop = 0, rate = 1, pan = 0, spread = 0.5, width = 2, amp = 0.5,
        attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
        /*gate = 1,*/ cutoff = 22e3, res = 0.01, fgain = 1.0, ftype = 0, bass = 0.0, pr = 1.0, fr = 1.0
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
        env = EnvGen.ar(Env.linen(attack, sustain, release), /*gate*/);
        FreeSelf.kr(TDelay.kr(Done.kr(env),freeSelfDelay));
        sig = ConvenientPitchShiftPA.ar(
            sig,
            Pitch.kr(sig)[0], //pitch tracking
            pr,
            fr
        );
        sig = DFM1.ar(in: sig, freq: cutoff, res: res, inputgain: fgain, type: ftype);
        sig = sig + (LPF.ar(sig, 100, bass));
        sig = SplayAz.ar(channels, sig, spread: spread, width: width, center: pan, level: amp);
        sig = LeakDC.ar(sig);
        Out.ar(out, (sig*env));
    }).add;

    SynthDef(("ConvenienceMonoPitchShiftScale_"++channels).asSymbol, {
        |
        bufnum, out, loop = 0, pan = 0, spread = 0.5, width = 2, amp = 0.5,
        attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
        /*gate = 1,*/ cutoff = 22e3, res = 0.01, fgain = 1.0, ftype = 0, bass = 0.0, basefreq=440, freq,
        pr = 1.0, fr = 1.0
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
        env = EnvGen.ar(Env.linen(attack, sustain, release), /*gate*/);
        FreeSelf.kr(TDelay.kr(Done.kr(env),freeSelfDelay));
        sig = ConvenientPitchShiftPA.ar(
            sig,
            Pitch.kr(sig)[0], //pitch tracking
            pr,
            fr
        );
        sig = DFM1.ar(in: sig, freq: cutoff, res: res, inputgain: fgain, type: ftype);
        sig = sig + (LPF.ar(sig, 100, bass));
        // sig = SplayAz.ar(channels, sig, spread: spread, width: width, center: pan, level: amp);
        sig = Pan2.ar(in:sig, pos:pan, level:amp);
        sig = LeakDC.ar(sig);
        Out.ar(out, (sig*env));
    }).add;

    SynthDef(("ConvenienceStereoPitchShiftScale_"++channels).asSymbol, {
        |
        bufnum, out, loop = 0, pan = 0, spread = 0.5, width = 2, amp = 0.5,
        attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
        /*gate = 1,*/ cutoff = 22e3, res = 0.01, fgain = 1.0, ftype = 0, bass = 0.0, basefreq=440, freq,
        pr = 1.0, fr = 1.0
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
        env = EnvGen.ar(Env.linen(attack, sustain, release), /*gate*/);
        FreeSelf.kr(TDelay.kr(Done.kr(env),freeSelfDelay));
        sig = ConvenientPitchShiftPA.ar(
            sig,
            Pitch.kr(sig)[0], //pitch tracking
            pr,
            fr
        );
        sig = DFM1.ar(in: sig, freq: cutoff, res: res, inputgain: fgain, type: ftype);
        sig = sig + (LPF.ar(sig, 100, bass));
        sig = SplayAz.ar(channels, sig, spread: spread, width: width, center: pan, level: amp);
        sig = LeakDC.ar(sig);
        Out.ar(out, (sig*env));
    }).add;
}
