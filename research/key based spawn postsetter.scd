
//
//             key based


Convenience.crawl("A:/LYDFILER/SKRAMMEL")
Convenience.crawl("C:/Users/na/Music/Giuseppe Ielasi - Rhetorical Islands")
Convenience.crawl
Convenience.list(1)
(
fork{
	Ndef(\combiner, { DC.ar([0,0]) });
	SynthDef('Zzzz', {
		|
		bufnum, loop = 0, basefreq=440, freq, pan = 0, spread = 0.5, width = 2, amp = 0.5,
		attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
		gate = 1, cutoff = 22e3, res = 0.01, fgain = 1.0, ftype = 0, bass = 0.0,
		detuneModulation = 0, detuneModulationSpeed = 1
		|
		var sig, rate, frames, env, fenv, file;
		frames = BufFrames.kr(bufnum);
		rate = freq/basefreq;
		sig = ConvenientBufferPlayer.ar(
			1,
			bufnum,
			rate*BufRateScale.kr(bufnum)+SinOsc.kr(detuneModulationSpeed).linlin(-1.0,1.0,detuneModulation.neg,detuneModulation),
			1,
			pos*frames,
			loop: loop
		);
		env = EnvGen.ar(Env.linen(attack, sustain, release), gate);
		fenv = EnvGen.ar(Env.perc(attack, release));
		FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
		sig = DFM1.ar(in: sig, freq: SinOsc.kr(0.5).abs*cutoff*fenv, res: res, inputgain: fgain, type: ftype);
		sig = sig + (LPF.ar(sig, 100, bass));
		sig = SplayAz.ar(2, sig, spread: spread, center: pan, level: amp);
		sig = LeakDC.ar(sig);
		sig = Limiter.ar(sig*env, 0.99);
		Out.ar(\out.kr, sig);
	}).add;

	//Ndef(\post).source = Pdef(\streamer); can jeg bare bruge streamer direktez?
	Ndef(\post).source = Ndef(\combiner);
	Ndef(\post)[1] = \filter -> {|in, pitch=0.5, pd=0,td=0| PitchShift.ar(in, 0.25, pitch, pd, td)};
	Ndef(\post)[2] = \filter -> {|in, roomsize = 10, revTime = 2, damping = 0.5| GVerb.ar(in, roomsize, revTime, damping)};
	Ndef(\post)[3] = \filter -> {|in, freqshift=0| FreqShift.ar(in, freqshift)};
	Ndef(\post)[4] = \filter -> ConvenientCatalog.getFx(\flanger);
	Ndef(\post)[5] = \filter -> ConvenientCatalog.getFx(\phaser);
	Ndef(\post)[6] = \filter -> ConvenientCatalog.getFx(\delay);
	Ndef(\post)[7] = \filter -> ConvenientCatalog.getFx(\chorus);
	Ndef(\post)[8] = \filter -> ConvenientCatalog.getFx(\binshift);
	Ndef(\post)[9] = \filter -> ConvenientCatalog.getFx(\shaper);
	Ndef(\post)[10] = \filter -> ConvenientCatalog.getFx(\comb);
	Ndef(\post)[11] = \filter -> {|in, rate=96000, bits=24| Decimator.ar(in, rate, bits)};
	Ndef(\post)[98] = \filter -> {|in| in*1.0}; // simple role gainer
	Ndef(\post)[99] = \filter -> {|in| Limiter.ar(LeakDC.ar(in),0.99)};
	Ndef(\post).playN([0,1])
}
)

Convenience.list
Convenience.get('Giuseppe_Ielasi___Rhetorical_Islands', 4)

Pdef(\streamer).stop
(
Pdef(\streamer,
	Pspawn(
		Pbind(
			\method, \par,
			\delta, Pwhite(0.5, 1.0, inf),
			\legato, Pwhite(0.3,1.0),
			\folder, 'oberheim', //Convenience.folderNum(5).postln,
			\file, Pwhite(170,180).trace(prefix: "\n\t file -> "), //Pstutter(10,Pfunc{100.rand}),
			//\key, 5-Pwrand([1,3,5,7,10], [1, 2, 3, 1, 3].normalizeSum, inf).trace(prefix: "\n\tkey -> "),
			\key, (Psinen(0.1)*12).round.trace(prefix: "\n\t key -> "),
			\octave, Pwrand([6,7],[1,1].normalizeSum.postln, inf),
			\detune, 0,//Psine(0.001).linlin(-1,1,-50,50).trace(prefix: "\n\tdetune -> "),
			\arraySize, 8,
			\pattern, Pfunc{ | envir |
				var times = rrand(1,2).postln;
				var folder = envir.folder ?? "envir folder not set";
				var file = envir.file ?? 1;
				var levels = Array.rand(envir.arraySize ?? 4, 0.0001, 1.0);
				var curve1 = rrand(-10,10);
				var curve2 = rrand(-10,10);
				var durs = Array.rand(envir.arraySize ?? 4, 0.2, 0.25);
				var dur_add = envir.legato.wrap(0.05,1.1) ?? 0.5;
				Pbind(
					\instrument, 'Zzzz',
					\group, Ndef(\combiner).group,
					\out, Ndef(\combiner).bus,
					\bufnum, Convenience.get(folder.asSymbol, file.asInteger),
					\renv, Pseg(
						Pseq(levels),
						Pseq(durs),
						curve1,
						times
					),
					\scram, Pseg(
						Pseq(levels.scramble),
						Pseq(durs.scramble),
						curve2,
						times
					),
					\dur, dur_add ?? 1,
					\degree, envir.key ?? 1,
					\octave, envir.octave ?? 4,
					\detune, envir.detune,
					\detuneModulation, 0.1,
					\detuneModulationSpeed, 0.5+Psinen(0.8)*5,
					\pos, 0.0, //Pkey(\renv).linlin(0.0,1.0,0.105,0.109)-0.04,
					\attack, Pwhite(0.001,0.002),
					\sustain, Pwhite(0.1,0.2+Pkey(\scram)*Pkey(\renv)),
					\release, 0.1,
					\cutoff, Pkey(\renv).linlin(0,1,100,22000),
					\res, Psinen(0.1)*0.8,
					\pan, rrand(-0.3,0.3).postln, //Pkey(\renv).linlin(0,1,-1.0,1.0),
					\amp, 0.5+( Pkey(\renv)*Pkey(\scram)*0.5 )
				)
			}
		)
	)
).play;
)

Ndef(\post).gui


(
Pdef(\postsetter,
	Pbind(
		\type, \set,
		\dur, Pwhite(0.05,0.1),
		\id, Pfunc({ Ndef(\post).group }),
		\args, Ndef(\post).controlKeys,
		\wet1, 0, // pitchshift
		\pitch, 0.5,
		\pd, 0.01,
		\td, 0.0,
		\wet2, 0.0, // reverb
		\revTime, 0.2,
		\roomsize, 0.3,
		\damping, 0.5,
		\wet3, 0.0, // freqshift
		\freqshift, 1900,
		\wet4, 0.0, // flanger
		\flangdelay, 0.5,
		\flangdepth, 1, \flangrate, 0.6, \flangfb, 0.4,
		\wet5, 0.1,// phaser
		\phaserrate, 0.7, \phaserdepth, 1.0,
		\wet6, 0.0,// delay
		\delay,  0.4, \delayfb,  0.5,
		\wet7, 0,// chorus
		\chpredelay, 1.0, \chrate,  0.7, \chdepth,  0.7, \chphasediff,  0.4,
		\wet8, 0.0,// binshift
		\binshift,  0.5,
		\wet9, 0.0,// shaper
		\shape, Pwhite(0.0,1.0),
		\wet10, 0.0,// comb
		\teeth, Pwhite(0.0,1.0), \comb, Pwhite(0.0,1.0),
		\wet98, 1, // gain
		\wet99, 1 // limiter
	)
).quant_(1).play;
)

(
Pdef(\postsetter,
	Pbind(
		\type, \set,
		\dur, Pwhite(0.1,2.3),
		\id, Pfunc({ Ndef(\post).group }),
		\masterWet, Pwhite(0.0,0.6),
		\args, Ndef(\post).controlKeys,
		\wet1, Pwhite(0.0,1.0) * Pkey(\masterWet),
		\pitch, Pwhite(0.1,1.5),
		\pd, Pwhite(0.0,1.0),
		\td, Pwhite(0.0,1.0),
		\wet2, Pwhite(0.0,1.0) * Pkey(\masterWet),
		\revTime, Pwhite(2.0,10.0),
		\roomsize, Pwhite(2.0,10.0),
		\damping, Pwhite(0.1,1.0),
		\wet3, Pwhite(0.0,1.0) * Pkey(\masterWet),
		\freqshift, Pwhite(-200.0,100.0),
		\wet4, Pwhite(0.0,1.0) * Pkey(\masterWet), // flanger
		\flangdelay, Pwhite(0.0,1.0),
		\flangdepth, Pwhite(0.0,1.0), \flangrate, Pwhite(0.0,1.0), \flangfb, Pwhite(0.0,1.0),
		\wet5, Pwhite(0.0,1.0) * Pkey(\masterWet),// phaser
		\phaserrate, Pwhite(0.0,1.0), \phaserdepth, Pwhite(0.0,1.0),
		\wet6, Pwhite(0.0,1.0) * Pkey(\masterWet),// delay
		\delay,  Pwhite(0.0,1.0), \delayfb,  Pwhite(0.0,1.0),
		\wet7, Pwhite(0.0,1.0) * Pkey(\masterWet),// chorus
		\chpredelay, Pwhite(0.0,1.0), \chrate,  Pwhite(0.0,1.0), \chdepth,  Pwhite(0.0,1.0), \chphasediff,  Pwhite(0.0,1.0),
		\wet8, Pwhite(0.0,1.0) * Pkey(\masterWet),// binshift
		\binshift,  Pwhite(0.0,1.0),
		\wet9, Pwhite(0.0,1.0) * Pkey(\masterWet),// shaper
		\shape, Pwhite(0.0,1.0),
		\wet10, Pwhite(0.0,1.0) * Pkey(\masterWet),// comb
		\teeth, Pwhite(0.0,1.0),
		\comb, Pwhite(0.0,1.0),
		\wet98, 8,
		\wet99, 1
	)
).quant_(1).play;
)


(
Pdef(\streamer).stop;
Ndef(\post).stop;
Ndef(\post).free;
Ndef(\post).clear;
Ndef(\combiner).stop;
Ndef(\combiner).free;
Ndef(\combiner).clear;
Pdef(\postsetter).stop;
)




