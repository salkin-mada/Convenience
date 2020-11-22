/*(
ServerTree.removeAll;
s.freeAll;
s.scope;
s.freqscope;
s.plotTree;
);
)*/


Convenience.crawl("C:/Users/na/Desktop/NEW MONA SIGLER/beinnings/MS_SAMP")
Convenience.list(1)


(
~out_bus = 2;
~efx_bus = Bus.audio(s,2);

Ndef(\efxes, {
	|in, predelay=0.01, revtime=0.0001, lpf=7000,mix=0.006,amp=1,out=0|
	var dry,wet,temp,sig;
	dry = In.ar(in, 2);
	temp = In.ar(in, 2);
	wet=0;
	temp=DelayN.ar(temp, 0.2, predelay);
	32.do {temp=AllpassN.ar(temp, 0.05, {Rand(0.001,0.05)}!2,revtime);
		temp=LPF.ar(temp,lpf);
		wet=wet+temp;
	};
	sig=XFade2.ar(dry,wet,mix*2-1,amp);
	sig=CompanderD.ar(sig,0.2,1.0,1/12,0.05,0.1,2);
	sig=Limiter.ar(sig,0.9);
	Out.ar(~out_bus,sig);
}).set(\in, ~efx_bus).fadeTime_(5).play;


SynthDef('Zzzz', {
	|
	bufnum, loop = 0, basefreq=440, freq, pan = 0, spread = 0.5, width = 2, amp = 0.5,
	attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
	gate = 1, cutoff = 22e3, res = 0.01, fgain = 1.0, ftype = 0, bass = 0.0,
	detuneModulation = 0, detuneModulationSpeed = 1, direction = 1
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
	Out.ar(~efx_bus, sig);
}).add;

~swingify = Prout({ |ev|
	var now, nextTime = 0, thisShouldSwing, nextShouldSwing = false, adjust;
	while { ev.notNil } {
		// current time is what was "next" last time
		now = nextTime;
		nextTime = now + ev.delta;
		thisShouldSwing = nextShouldSwing;
		nextShouldSwing = ((nextTime absdif: nextTime.round(ev[\swingBase])) <= (ev[\swingThreshold] ? 0)) and: {
			(nextTime / ev[\swingBase]).round.asInteger.odd
		};
		adjust = ev[\swingBase] * ev[\swingAmount];
		// an odd number here means we're on an off-beat
		if(thisShouldSwing) {
			ev[\timingOffset] = (ev[\timingOffset] ? 0) + adjust;
			// if next note will not swing, this note needs to be shortened
			if(nextShouldSwing.not) {
				ev[\sustain] = ev.use { ~sustain.value } - adjust;
			};
		} {
			// if next note will swing, this note needs to be lengthened
			if(nextShouldSwing) {
				ev[\sustain] = ev.use { ~sustain.value } + adjust;
			};
		};
		ev = ev.yield;
	};
});
);


(
TempoClock.tempo= 148 / 120;

x=Array.fill(4, {rrand(0.1, 1.0)}).normalizeSum; // weights

//~scale = Scale.mixolydian;
~scale = Scale.dorian;
//~scale = Scale.ionian;

~chords = [\Cm_eb, \Fm, \Gm, \Cm_g, \Cm_eb, \Fm, \Gm_d, \Cm].chordProgDegrees(~scale);

~chord_structure = [

	Pstep([~chords[0], ~chords[1]], 1, 4),
	Pstep([~chords[2], ~chords[3]], 2, 2),
	Pstep([~chords[4], ~chords[5]], 1, 4),
	Pstep([~chords[6], ~chords[7]], 2, 2),


	/*Pstep([\Cmajor_B,\Cmajor_Ds], 4, 1),
	Pstep([\Gmajor_E,\Gmajor_Ds], 4, 1),
	Pstep([\Dminor_E,\Dminor_Ds], 4, 1),
	Pstep([\Dminor_B,\Dminor_B], 4, 1),

	Pstep([\Bmajor7_B,\Bmajor7_Ds], 4, 2),
	Pstep([\Ebmajor7_Eb,\Ebmajor7_G,], 4, 1),
	Pstep([\Bmajor7_B,\Bmajor7_Ds], 4, 2),
	Pstep([\Ebmajor7_Eb,\Ebmajor7_G,], 4, 1),

	Pstep([\Dminor_B,\Dminor_B], 4, 1),*/

	/*Pwrand([\Bmajor7_B,\Bmajor7_Ds,\Bmajor7_Fs,\Bmajor7_As],x,16),
	Pwrand([\Ebmajor7_Eb,\Ebmajor7_G,\Ebmajor7_Bb,\Ebmajor7_D],x, 16),
	Pwrand([\Bmajor7_B,\Bmajor7_Ds,\Bmajor7_Fs,\Bmajor7_As],x,16),
	Pwrand([\Ebmajor7_Eb,\Ebmajor7_G,\Ebmajor7_Bb,\Ebmajor7_D],x, 16),
	Pwrand([\Aminor7_A,\Aminor7_C,\Aminor7_E,\Aminor7_G],x, 16),
	Pwrand([\Bbdom7_Bb,\Bbdom7_D,\Bbdom7_F,\Bbdom7_A],x, 16)*/
];

~melody_structure = [

	Pstep([Prand(~chords[0]),Prand(~chords[1])], 1, 4),
	Pstep([Prand(~chords[2]),Prand(~chords[3])], 2, 2),
	Pstep([Prand(~chords[4]),Prand(~chords[5])], 1, 4),
	Pstep([Prand(~chords[6]),Prand(~chords[7])], 2, 2),

	/*Pstep([Prand(\Cmajor_B.asNotes),Prand(\Cmajor_Ds.asNotes)], 4, 1),
	Pstep([Prand(\Gmajor_E.asNotes),Prand(\Gmajor_Ds.asNotes)], 4, 1),
	Pstep([Prand(\Dminor_E.asNotes),Prand(\Dminor_Ds.asNotes)], 4, 1),
	Pstep([Prand(\Dminor_B.asNotes),Prand(\Dminor_B.asNotes)], 4, 1),

	Pstep([Prand(\Bmajor7_B.asNotes),Prand(\Bmajor7_Ds.asNotes)], 4, 2),
	Pstep([Prand(\Ebmajor7_Eb.asNotes),Prand(\Ebmajor7_G.asNotes)], 4, 1),
	Pstep([Prand(\Bmajor7_B.asNotes),Prand(\Bmajor7_Ds.asNotes)], 4, 2),
	Pstep([Prand(\Ebmajor7_Eb.asNotes),Prand(\Ebmajor7_G.asNotes)], 4, 1),

	Pstep([Prand(\Dminor_B.asNotes),Prand(\Dminor_B.asNotes)], 4, 1),*/

];

Pdef(\chord1,
	Ppar([
		Pseq([
			Pchain(
				~swingify,
				PmonoArtic(
					\Zzzz, // instrument
					\scale, ~scale,
					\folder, 'MS_SAMP',
					\file, 2,
					\octave, 7,
					\pos, Psinen(0.01),
					\pan, Pwhite(-0.25,0.25),
					\dur, Pseq([1/2,1/2,1/2,1/4,5/4,1/2,1/2,1/2,1/2,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/4,3/4],inf),
					\legato, Prand([1/1.2,1/1.5],inf),
					\degree,
					Pseq(NoteSymbol.asDegree(~chord_structure),inf).trace(prefix: 'chord2'),
					\attack, Pexprand(0.07, 1,inf),
					\sustain, Pexprand(0.1, 1,inf),
					\release, Pexprand(0.1, 2,inf),
					\amp, Pwhite(0.03,0.07)
				),
				(swingBase: 0.25, swingAmount: 0.7)
			),
			Pfuncn({ q.stop; Event.silent(0) }, 1)
		])
	])
).play(quant: 1/8);


Pdef(\chord2,
	Ppar([
		Pseq([
			Pchain(
				~swingify,
				PmonoArtic(
					\Zzzz, // instrument
					\scale, ~scale,
					\folder, 'MS_SAMP',
					\file, 2,
					\octave, 7,
					\pos, Psinen(0.01),
					\pan, Pwhite(-0.25,0.25),
					\dur, Pseq([1/2,1/2,1/2,1/4,5/4,1/2,1/2,1/2,1/2,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/4,3/4],inf),
					\legato, Prand([1/1.2,1/1.5],inf),
					\degree,
					Pseq(NoteSymbol.asDegree(~chord_structure),inf).trace(prefix: 'chord2'),
					\attack, Pexprand(0.07, 1,inf),
					\sustain, Pexprand(0.1, 1,inf),
					\release, Pexprand(0.1, 2,inf),
					\amp, Pwhite(0.03,0.07)
				),
				(swingBase: 0.25, swingAmount: 0.7)
			),
			Pfuncn({ q.stop; Event.silent(0) }, 1)
		])
	])
).play(quant: 1/8)
;


Pdef(\melody1,
	Ppar([
		Pseq([
			Pchain(
				~swingify,
				PmonoArtic(
					\Zzzz, // instrument
					\scale, ~scale,
					\folder, 'MS_SAMP',
					\file, 2,
					\octave, Prand([6,7], inf),
					\pos, Psinen(0.001).linlin(0,1,0.35,0.75),
					\pan, Pwhite(-0.25,0.25),
					\dur, Pseq([1/2,1/2,1/2,1/4,5/4,1/2,1/2,1/2,1/2,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/4,3/4],inf),
					\legato, Prand([1/1.2,1/1.5],inf),
					\degree,
					(
						Pseq(ChordSymbol.degreeProgression(~melody_structure),inf)
					).trace(prefix: 'melody1: '),

					\attack, Pexprand(0.07, 0.2,inf),
					\sustain, Pexprand(0.1, 3,inf),
					\release, Pexprand(0.1, 2,inf),
					\direction, 1,
				),
				(swingBase: 0.25, swingAmount: 0.7)
			),
			Pfuncn({ q.stop; Event.silent(0) }, 1)
		])
	])
).play(quant: 1/8);

Pdef(\melody2,
	Ppar([
		Pseq([
			Pchain(
				~swingify,
				PmonoArtic(
					\Zzzz, // instrument
					\scale, ~scale,
					\folder, 'MS_SAMP',
					\file, 2,
					\octave, Prand([6,7], inf),
					\pos, Psinen(0.001).linlin(0,1,0.35,0.75),
					\pan, Pwhite(-0.25,0.25),
					\dur, Prand([1/2,1/2,1/2,1/4,5/4,1/2,1/2,1/2,1/2,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/4,3/4],inf),
					\legato, Prand([1/1.2,1/1.5],inf),
					\degree,
					(
						Pseq(ChordSymbol.degreeProgression(~melody_structure),inf)
					).trace(prefix: 'melody2: '),

					\attack, Pexprand(0.07, 0.2,inf),
					\sustain, Pexprand(0.3, 3,inf),
					\release, Pexprand(0.1, 2,inf),
					\direction, 1,
				),
				(swingBase: 0.25, swingAmount: 0.7)
			),
			Pfuncn({ q.stop; Event.silent(0) }, 1)
		])
	])
).play(quant: 1/8);
);

Pdef(\chord1).stop;
Pdef(\chord2).stop;
Pdef(\melody1).stop;
Pdef(\melody2).stop;

Pdef(\melody).clear









///////////////////////
///////////////////////
///////////////////////
///////////////////////
///////////////////////
///////////////////////


Convenience.crawl("X:/rod fra 23-11/R-26 ROD d. 02-08-2013/0720_132256.PJT")
Convenience.list(1)

(
Pdef(\chord1).stop;
Pdef(\chord2).stop;
Pdef(\melody1).stop;
Pdef(\melody2).stop;
Pdef(\chord1).free;
Pdef(\chord2).free;
Pdef(\melody1).free;
Pdef(\melody2).free;
)

(

~out_bus = 2;
~efx_bus = Bus.audio(s,2);

Ndef(\efxes, {
	|in, predelay=0.01, revtime=0.0001, lpf=7000,mix=0.006,amp=1,out=0|
	var dry,wet,temp,sig;
	dry = In.ar(in, 2);
	temp = In.ar(in, 2);
	wet=0;
	temp=DelayN.ar(temp, 0.2, predelay);
	32.do {temp=AllpassN.ar(temp, 0.05, {Rand(0.001,0.05)}!2,revtime);
		temp=LPF.ar(temp,lpf);
		wet=wet+temp;
	};
	sig=XFade2.ar(dry,wet,mix*2-1,amp);
	sig=CompanderD.ar(sig,0.2,1.0,1/12,0.05,0.1,2);
	sig=Limiter.ar(sig,0.9);
	Out.ar(~out_bus,sig);
}).set(\in, ~efx_bus).fadeTime_(5).play;

SynthDef('Zzzz', {
	|
	bufnum, loop = 0, basefreq=440, freq, pan = 0, spread = 0.5, width = 2, amp = 0.5,
	attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
	gate = 1, cutoff = 22e3, res = 0.01, fgain = 1.0, ftype = 0, bass = 0.0,
	detuneModulation = 0, detuneModulationSpeed = 1, direction = 1
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
	Out.ar(~efx_bus, sig);
}).add;

~swingify = Prout({ |ev|
	var now, nextTime = 0, thisShouldSwing, nextShouldSwing = false, adjust;
	while { ev.notNil } {
		// current time is what was "next" last time
		now = nextTime;
		nextTime = now + ev.delta;
		thisShouldSwing = nextShouldSwing;
		nextShouldSwing = ((nextTime absdif: nextTime.round(ev[\swingBase])) <= (ev[\swingThreshold] ? 0)) and: {
			(nextTime / ev[\swingBase]).round.asInteger.odd
		};
		adjust = ev[\swingBase] * ev[\swingAmount];
		// an odd number here means we're on an off-beat
		if(thisShouldSwing) {
			ev[\timingOffset] = (ev[\timingOffset] ? 0) + adjust;
			// if next note will not swing, this note needs to be shortened
			if(nextShouldSwing.not) {
				ev[\sustain] = ev.use { ~sustain.value } - adjust;
			};
		} {
			// if next note will swing, this note needs to be lengthened
			if(nextShouldSwing) {
				ev[\sustain] = ev.use { ~sustain.value } + adjust;
			};
		};
		ev = ev.yield;
	};
});

TempoClock.tempo= 148 / 120;

~folder = 'MS_SAMP';
~file = 1;

x=Array.fill(4, {rrand(0.1, 1.0)}).normalizeSum; // weights

~scale = Scale.mixolydian;
//~scale = Scale.dorian;
//~scale = Scale.pelog;

~pos = Psinen(0.01).linlin(0.0,1.0,0.22,0.25);
~octave_offset = -1;
~amp_max = 0.2;

/*
ChordSymbol.shapes.keys;
*/

~chords = [\Bm7,\D,\Dm7,\G6,\D6,\Em,\A,\Em7,\Bm,\Gsus2,\A_d,\Bsus2,\Asus4,\Asus2].chordProgDegrees(~scale);

~chord_structure = [
	Pstep([~chords[0], ~chords[1]], 1, 4),
	Pstep([~chords[2], ~chords[3]], 2, 2),
	Pstep([~chords[4], ~chords[5]], 1, 4),
	Pstep([~chords[6], ~chords[7]], 2, 2),
	Pstep([~chords[8], ~chords[9]], 2, 2),
	Pstep([~chords[10], ~chords[11]], 2, 2),
	Pstep([~chords[12], ~chords[13]], 2, 2),
];

~melody_structure = [
	Pstep([Prand(~chords[0]),Prand(~chords[1])], 1, 4),
	Pstep([Prand(~chords[2]),Prand(~chords[3])], 2, 2),
	Pstep([Prand(~chords[4]),Prand(~chords[5])], 1, 4),
	Pstep([Prand(~chords[6]),Prand(~chords[7])], 2, 2),
	Pstep([Prand(~chords[8]),Prand(~chords[9])], 2, 2),
	Pstep([Prand(~chords[10]),Prand(~chords[11])], 2, 2),
	Pstep([Prand(~chords[12]),Prand(~chords[13])], 2, 2),
];


Pdef(\chord1,
	Ppar([
		Pseq([
			Pchain(
				~swingify,
				PmonoArtic(
					\Zzzz, // instrument
					\scale, ~scale,
					\folder, ~folder,
					\file, ~file,
					\octave, 7+~octave_offset,
					\pos, ~pos,
					\pan, Pwhite(-0.25,0.25),
					\dur, Pseq([1/2,1/2,1/2,1/4,5/4,1/2,1/2,1/2,1/2,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/4,3/4],inf),
					\legato, Prand([1/1.2,1/1.5],inf),
					\degree,
					Pseq(NoteSymbol.asDegree(~chord_structure),inf).trace(prefix: 'chord2'),
					\attack, Pexprand(0.07, 1,inf),
					\sustain, Pexprand(0.1, 1,inf),
					\release, Pexprand(0.1, 2,inf),
					\amp, Pwhite(~amp_max-0.1,~amp_max)
				),
				(swingBase: 0.25, swingAmount: 0.7)
			),
			Pfuncn({ q.stop; Event.silent(0) }, 1)
		])
	])
).play(quant: 1/8);

Pdef(\chord2,
	Ppar([
		Pseq([
			Pchain(
				~swingify,
				PmonoArtic(
					\Zzzz, // instrument
					\scale, ~scale,
					\folder, ~folder,
					\file, ~file,
					\octave, 7+~octave_offset,
					\pos, ~pos,
					\pan, Pwhite(-0.25,0.25),
					\dur, Pseq([1/2,1/2,1/2,1/4,5/4,1/2,1/2,1/2,1/2,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/4,3/4],inf),
					\legato, Prand([1/1.2,1/1.5],inf),
					\degree,
					Pseq(NoteSymbol.asDegree(~chord_structure),inf).trace(prefix: 'chord2'),
					\attack, Pexprand(0.07, 1,inf),
					\sustain, Pexprand(0.1, 1,inf),
					\release, Pexprand(0.1, 2,inf),
					\amp, Pwhite(~amp_max-0.1,~amp_max)
				),
				(swingBase: 0.25, swingAmount: 0.7)
			),
			Pfuncn({ q.stop; Event.silent(0) }, 1)
		])
	])
).play(quant: 1/8)
;


Pdef(\melody1,
	Ppar([
		Pseq([
			Pchain(
				~swingify,
				PmonoArtic(
					\Zzzz, // instrument
					\scale, ~scale,
					\folder, ~folder,
					\file, ~file,
					\octave, Prand([6,7], inf)+~octave_offset,
					\pos, ~pos,
					\pan, Pwhite(-0.25,0.25),
					\dur, Pseq([1/2,1/2,1/2,1/4,5/4,1/2,1/2,1/2,1/2,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/4,3/4],inf),
					\legato, Prand([1/1.2,1/1.5],inf),
					\degree,
					(
						Pseq(ChordSymbol.degreeProgression(~melody_structure),inf)
					).trace(prefix: 'melody1: '),

					\attack, Pexprand(0.07, 0.2,inf),
					\sustain, Pexprand(0.1, 3,inf),
					\release, Pexprand(0.1, 2,inf),
					\amp, Pwhite(~amp_max-0.1,~amp_max)
				),
				(swingBase: 0.25, swingAmount: 0.7)
			),
			Pfuncn({ q.stop; Event.silent(0) }, 1)
		])
	])
).play(quant: 1/8);

Pdef(\melody2,
	Ppar([
		Pseq([
			Pchain(
				~swingify,
				PmonoArtic(
					\Zzzz, // instrument
					\scale, ~scale,
					\folder, ~folder,
					\file, ~file,
					\octave, Prand([6,7], inf)+~octave_offset,
					\pos, ~pos,
					\pan, Pwhite(-0.25,0.25),
					\dur, Prand([1/2,1/2,1/2,1/4,5/4,1/2,1/2,1/2,1/2,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/4,3/4],inf),
					\legato, Prand([1/1.2,1/1.5],inf),
					\degree,
					(
						Pseq(ChordSymbol.degreeProgression(~melody_structure),inf)
					).trace(prefix: 'melody2: '),

					\attack, Pexprand(0.07, 0.2,inf),
					\sustain, Pexprand(0.3, 3,inf),
					\release, Pexprand(0.1, 2,inf),
					\amp, Pwhite(~amp_max-0.1,~amp_max)
				),
				(swingBase: 0.25, swingAmount: 0.7)
			),
			Pfuncn({ q.stop; Event.silent(0) }, 1)
		])
	])
).play(quant: 1/8);
);












///////////////////////
///////////////////////
///////// 3 ///////////
///////////////////////
///////////////////////
///////////////////////


Convenience.crawl("X:/rod fra 23-11/R-26 ROD d. 02-08-2013",2)
Convenience.crawl("A:/LYDFILER/mona sigler generate spasm",2)
Convenience.list(1)

(
Pdef(\chord1).stop;
Pdef(\chord2).stop;
Pdef(\melody1).stop;
Pdef(\melody2).stop;
Pdef(\melody3).stop;
Pdef(\chord1).free;
Pdef(\chord2).free;
Pdef(\melody1).free;
Pdef(\melody2).free;
Pdef(\melody3).free;
)

(

~out_bus = 2; // stereereereoo
~efx_bus = Bus.audio(s,2);

Ndef(\efxes, {
	|in, predelay=0.01, revtime=0.0001, lpf=7000,mix=0.001,amp=1,out=0|
	var dry,wet,temp,sig;
	dry = In.ar(in, 2);
	temp = In.ar(in, 2);
	wet=0;
	temp=DelayN.ar(temp, 0.2, predelay);
	32.do {temp=AllpassN.ar(temp, 0.05, {Rand(0.001,0.05)}!2,revtime);
		temp=LPF.ar(temp,lpf);
		wet=wet+temp;
	};
	sig=XFade2.ar(dry,wet,mix*2-1,amp);
	sig=CompanderD.ar(sig,0.2,1.0,1/12,0.05,0.1,2);
	sig=Limiter.ar(sig,0.9);
	Out.ar(~out_bus,sig);
}).set(\in, ~efx_bus).fadeTime_(5).play;

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
	Out.ar(~efx_bus, sig);
}).add;

~swingify = Prout({ |ev|
	var now, nextTime = 0, thisShouldSwing, nextShouldSwing = false, adjust;
	while { ev.notNil } {
		// current time is what was "next" last time
		now = nextTime;
		nextTime = now + ev.delta;
		thisShouldSwing = nextShouldSwing;
		nextShouldSwing = ((nextTime absdif: nextTime.round(ev[\swingBase])) <= (ev[\swingThreshold] ? 0)) and: {
			(nextTime / ev[\swingBase]).round.asInteger.odd
		};
		adjust = ev[\swingBase] * ev[\swingAmount];
		// an odd number here means we're on an off-beat
		if(thisShouldSwing) {
			ev[\timingOffset] = (ev[\timingOffset] ? 0) + adjust;
			// if next note will not swing, this note needs to be shortened
			if(nextShouldSwing.not) {
				ev[\sustain] = ev.use { ~sustain.value } - adjust;
			};
		} {
			// if next note will swing, this note needs to be lengthened
			if(nextShouldSwing) {
				ev[\sustain] = ev.use { ~sustain.value } + adjust;
			};
		};
		ev = ev.yield;
	};
});

TempoClock.tempo= 168 / 120;

/*~folder = '0720_001044.PJT';
~file = 1;
~pos = Psinen(0.01).linlin(0.0,1.0,0.885,0.89).trace;
*/

~folder = Convenience.folderNum(20);
~file = 27;
~pos = Psinen(0.05).linlin(0.0,1.0,0.1,0.92).trace;
//~pos = Psinen(0.05).linlin(0.0,1.0,0.45,0.46);
x=Array.fill(4, {rrand(0.1, 1.0)}).normalizeSum; // weights

~scale = Scale.mixolydian;
//~scale = Scale.dorian;
//~scale = Scale.pelog;


~octave_offset = Pstep([2], Pseq([3,1,2]), inf);
~amp_min = 0.19;
~amp_max = 0.3;

/*
ChordSymbol.shapes.keys;
*/

//~chords = [\Bm7,\D,\Dm7,\G6,\D6,\Em,\A,\Em7,\Bm,\Gsus2,\A_d,\Bsus2,\Asus4,\Asus2].chordProgDegrees(~scale);
//~chords = [\Am7,\F6,\Am_c,\Am,\G,\Gsus4,\G_b,\Asus4,\Bm,\Gsus2,\A_d,\Bsus2,\Asus4,\Asus2].chordProgDegrees(~scale);
~chords = [\F,\Dm,\Fsus2_d,\C,\Am,\Bb6,\Bbm7,\Fsus2,\Dsus2,\F,\Dm,\Fsus2_d,\C,\Am].chordProgDegrees(~scale);

~chord_structure = [
	Pstep([~chords[0], ~chords[1], ~chords[4]], 4, 1)
	/*Pstep([~chords[0], ~chords[1]], 4, 1),
	Pstep([~chords[2], ~chords[3]], 2, 2),
	Pstep([~chords[0], ~chords[5]], 4, 1),
	Pstep([~chords[4], ~chords[5]], 4, 1),
	Pstep([~chords[6], ~chords[7]], 2, 2),
	Pstep([~chords[8], ~chords[9]], 2, 2),
	Pstep([~chords[10], ~chords[11]], 2, 2),
	Pstep([~chords[12], ~chords[13]], 2, 2),
	Pstep([~chords[6], ~chords[1]], Pseq([1,4]), 2),*/
];

~melody_structure = [
	Pstep([Prand(~chords[2]),Prand(~chords[5])], 4, 1)
	/*Pstep([Prand(~chords[0]),Prand(~chords[1])], 4, 1),
	Pstep([Prand(~chords[2]),Prand(~chords[3])], 2, 2),
	Pstep([Prand(~chords[0]),Prand(~chords[5])], 4, 1),
	Pstep([Prand(~chords[4]),Prand(~chords[5])], 4, 1),
	Pstep([Prand(~chords[6]),Prand(~chords[7])], 2, 2),
	Pstep([Prand(~chords[8]),Prand(~chords[9])], 2, 2),
	Pstep([Prand(~chords[10]),Prand(~chords[11])], 2, 2),
	Pstep([Prand(~chords[12]),Prand(~chords[13])], 2, 2),
	Pstep([Prand(~chords[6]),Prand(~chords[11])], Pseq([1,4]), 2),*/
];


Pdef(\chord1,
	Ppar([
		Pseq([
			Pchain(
				~swingify,
				/*Pbind(
					\instrument, \Zzzz,*/
				PmonoArtic(
					\Zzzz, // instrument
					\scale, ~scale,
					\bufnum, Convenience.get(~folder.asSymbol, ~file.asInteger),
					\octave, 7+~octave_offset,
					\pos, ~pos,
					\pan, Pwhite(-0.25,0.25),
					\dur, Pseq([1/2,1/2,1/2,1/4,5/4,1/2,1/2,1/2,1/2,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/4,3/4],inf),
					\legato, Prand([1/1.2,1/1.5],inf),
					\degree,
					Pseq(NoteSymbol.asDegree(~chord_structure),inf).trace(prefix: 'chord2'),
					\attack, Pexprand(0.07, 1,inf),
					\sustain, Pexprand(0.1, 1,inf),
					\release, Pexprand(0.1, 2,inf),
					\amp, Pwhite(~amp_min,~amp_max)
				),
				(swingBase: 0.25, swingAmount: 0.7)
			),
			Pfuncn({ q.stop; Event.silent(0) }, 1)
		])
	])
)//.play(quant: 1/8)
;

Pdef(\chord2,
	Ppar([
		Pseq([
			Pchain(
				~swingify,
				PmonoArtic(
					\Zzzz, // instrument
					\scale, ~scale,
					\bufnum, Convenience.get(~folder.asSymbol, ~file.asInteger),
					\octave, 7+~octave_offset,
					\pos, ~pos,
					\pan, Pwhite(-0.25,0.25),
					\dur, Pseq([1/2,1/2,1/2,1/4,5/4,1/2,1/2,1/2,1/2,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/2,1/4,5/4,1/4,3/4],inf),
					\legato, Prand([1/1.2,1/1.5],inf),
					\degree,
					Pseq(NoteSymbol.asDegree(~chord_structure),inf).trace(prefix: 'chord2'),
					\attack, Pexprand(0.07, 1,inf),
					\sustain, Pexprand(0.1, 1,inf),
					\release, Pexprand(0.1, 2,inf),
					\amp, Pwhite(~amp_min,~amp_max)
				),
				(swingBase: 0.25, swingAmount: 0.7)
			),
			Pfuncn({ q.stop; Event.silent(0) }, 1)
		])
	])
)//.play(quant: 1/8)
;


Pdef(\melody1,
	Ppar([
		Pseq([
			Pchain(
				~swingify,
				/*PmonoArtic(
					\Zzzz, // instrument*/
				Pbind(
					\instrument, \Zzzz,
					\scale, ~scale,
					\bufnum, Convenience.get(~folder.asSymbol, ~file.asInteger),
					\octave, Prand([6,7], inf)+~octave_offset,
					\pos, ~pos,
					\pan, Pwhite(-0.25,0.25),
					\dur, Prand([1/4,5/4,1/2,1/4,5/4],inf),
					\legato, Prand([1/1.2,1/1.5],inf),
					\detuneModulation, 0.0001, \detuneModulationSpeed, Pwhite(5,10),
					\degree,
					(
						Pseq(ChordSymbol.degreeProgression(~melody_structure),inf)
					).trace(prefix: 'melody1: '),

					\attack, Pexprand(0.07, 0.2,inf),
					\sustain, Pexprand(2.1, 3,inf),
					\release, Pexprand(0.1, 2,inf),
					\amp, Pwhite(~amp_min,~amp_max)
				),
				(swingBase: 0.25, swingAmount: 0.7)
			),
			Pfuncn({ q.stop; Event.silent(0) }, 1)
		])
	])
)//.play(quant: 1/8)
;

Pdef(\melody2,
	Ppar([
		Pseq([
			Pchain(
				~swingify,
				/*PmonoArtic(
					\Zzzz, // instrument*/
				Pbind(
					\instrument, \Zzzz,
					\scale, ~scale,
					\bufnum, Convenience.get(~folder.asSymbol, ~file.asInteger),
					\octave, Prand([6,7], inf)+~octave_offset,
					\pos, ~pos,
					\pan, Pwhite(-0.25,0.25),
					\dur, Prand([1/4,5/4,1/2,1/4,5/4],inf),
					\legato, Prand([1/1.2,1/1.5],inf),
					\detuneModulation, 0.0001, \detuneModulationSpeed, Pwhite(0.2,1),
					\degree,
					(
						Pseq(ChordSymbol.degreeProgression(~melody_structure),inf)
					).trace(prefix: 'melody2: '),

					\attack, Pexprand(0.07, 0.2,inf),
					\sustain, Pexprand(2.3, 3,inf),
					\release, Pexprand(0.1, 2,inf),
					\amp, Pwhite(~amp_min,~amp_max)
				),
				(swingBase: 0.25, swingAmount: 0.7)
			),
			Pfuncn({ q.stop; Event.silent(0) }, 1)
		])
	])
).play(quant: 1/8);

Pdef(\melody3,
	Ppar([
		Pseq([
			Pchain(
				~swingify,
				/*PmonoArtic(
					\Zzzz, // instrument*/
				Pbind(
					\instrument, \Zzzz,
					\scale, ~scale,
					\bufnum, Convenience.get(~folder.asSymbol, ~file.asInteger),
					\octave, Prand([6,7,8], inf)+~octave_offset,
					\pos, ~pos,
					\pan, Pwhite(-0.25,0.25),
					\dur, Prand([1/4,5/4,1/2,1/4,5/4],inf),
					\legato, Prand([1/1.2,1/1.5],inf),
					//\detuneModulation, 1, \detuneModulationSpeed, 10,
					\detuneModulation, Psinen(0.08).linlin(0,1,0.0001,0.0004), \detuneModulationSpeed, Pwhite(0.01,0.3),
					\degree,
					(
						Pseq(ChordSymbol.degreeProgression(~melody_structure),inf)
					).trace(prefix: 'melody2: '),

					\attack, Pexprand(0.07, 0.2,inf),
					\sustain, Pexprand(1.3, 3,inf),
					\release, Pexprand(0.1, 2,inf),
					\amp, Pwhite(~amp_min,~amp_max)
				),
				(swingBase: 0.25, swingAmount: 0.7)
			),
			Pfuncn({ q.stop; Event.silent(0) }, 1)
		])
	])
).play(quant: 1/8);
);


