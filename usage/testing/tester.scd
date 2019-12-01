Convenience.crawl("A:/LYDFILER/pingpong pickup/dynamic ping split", 1)
Convenience.crawl("A:/LYDFILER/Drums", 4)
Convenience.list(gui: true)

Convenience.free()

Pspawn
Pspeed

ZzzZzZzzzZz.list
v = Convenience;
Convenience.s(\happy)
(
Convenience.p(\happy, dur: 0.25, amp: 0.6, pan: Pwhite(-0.3,0.3), pos: 0.0/*Pwhite(0.0,0.8)*/,
	folder: Pfunc({Convenience.randomFolder}), index: Pwhite(0,1e3).ceil,
	tuningOnOff: 1,
	degree: Pstep([-2,1,5,7,1,10], 3, inf),
	octave: Prand([6,7,8], inf),
	out:[30])
)
Convenience.modul(\happy, [30,31], 25)
Convenience.lfos(20, 0.4)
Convenience.map(\happy)

ZzzZzZzzzZz.list
(
Convenience.p(\happy, dur: 0.25, amp: 0.6, pan: Pwhite(-0.3,0.3), pos: 0.0/*Pwhite(0.0,0.8)*/,
	folder: \classic_808, index: Pwhite(0,1e3).ceil,
	tuningOnOff: 1,
	degree: Pstep([-2,1,5,7,1,10], 3, inf),
	octave: Prand([6,7], inf),
	out:[30])
)
(
Convenience.p(\happy, dur: 0.75, amp: 0.6, pan: Pwhite(-0.3,0.3), pos: 0.0/*Pwhite(0.0,0.8)*/,
	folder: \classic_808, index: Pwhite(0,1e3).ceil,
	tuningOnOff: 1,
	degree: Pstep([-2,1,5,7,1,10], 3, inf),
	octave: Prand([6,7], inf),
	out:[30])
)
(
Convenience.p(\happy, dur: 0.125, amp: 0.6, pan: Pwhite(-0.3,0.3), pos: 0.0/*Pwhite(0.0,0.8)*/,
	folder: \Roland_TR_606, index: Pwhite(0,1e3).ceil,
	tuningOnOff: 1,
	degree: Pstep([-2,1,5,7,1,10], 3, inf),
	octave: Prand([6,7], inf),
	out:[30])
)

Convenience.modul(\lolz, [30,31], 15)
Convenience.map(\lolz, 1.0)





~supportedExtensions.includes(PathName("A:\LYDFILER\Drums\Basement Drums\Samples\._BG mf Snare1 Top 01.wav").extension.toLower.asSymbol)
~supportedExtensions = #[\wav, \wave, \aif, \aiff, \flac];
~folder = PathName("A:/LYDFILER/Drums/Basement Drums/Samples");

(
~files = ~folder.entries.select { | file |
	var hiddenFile;
	var result;

	file.fileName.do{ | char, i | if(char.isPunct and: i == 0, {"found a hidden file - avoiding".postln; hiddenFile = true})};

	if(hiddenFile.asBoolean.not, {
		result = ~supportedExtensions.includes(file.extension.toLower.asSymbol);
	}, {result = false});
	result;
};
)
~files.do{|file, i|  if(i<6, {file.postln})}




// lav en ryd opp metode for de pdeffer der laves i .p metoden!!!!!!!!!!!!!!!!

12.do{arg i;Convenience.s(("time"++i).asSymbol); ("time"++i).postln;}

/// further testing
Convenience.crawl("A:/LYDFILER/Drums", 4)
ZzzZzZzzzZz.list(true)
(
Convenience.p(\time4, tempo: 154/60,
	dur: Pseq([Rest(5),1,3,5,2,5,Rest(12)], inf)/4,
	amp: 1.9, pan: Pwhite(0.1,0.8), pos: 0.0,
	folder: \Bassdrums, index: 9,
	rate: 0.8,
	out: 2);
Convenience.p(\time5, tempo: 154/60,
	dur: Pseq([Rest(5),1,3,5,2,5,Rest(12)], inf)/4,
	amp: 1.4, pan: Pwhite(-0.8,-0.1), pos: 0.0,
	folder: \Bassdrums, index: 14,
	rate: 1,
	release: 2.0,
	out: 2);
Convenience.p(\time6, tempo: 154/60,
	dur: Pseq([Rest(5),1,3,5,2,5,Rest(12)], inf)/4,
	amp: 1.9,
	folder: \SP_SUBKIKS, index: 2,
	rate: 0.3,
	release: 3.0,
	out: 2);
Convenience.p(\time7, tempo: 154/60,
	dur: Pseq([Rest(5),1,3,5,2,5,Rest(12)], inf)/4,
	amp: 0.3,
	folder: \classic_808, index: 15,
	pitchShiftOnOff: 1,
	pitchRatio: 2.2,
	formantRatio: 0.3,
	rate: 0.3,
	release: 2.0,
	out: 2);
Convenience.p(\time8, tempo: 154/60,
	dur: Pseq([Rest(5),1,3,5,2,5,Rest(12)], inf)/4,
	amp: 0.4,
	folder: \classic_808, index: 28,
	pitchShiftOnOff: 0,
	pitchRatio: 1.2,
	formantRatio: 0.8,
	rate: 2,
	release: 2.0,
	out: 2);
Convenience.p(\time1, tempo: 154/60,
	dur: Pseq([1,3,5,2,5, Rest(5)], inf)/2*0.5,
	stretch: Pwhite(0.99,1.01),
	amp: 0.9, pan: Pwhite(-0.3,0.3), pos: 0.0,
	folder: Pstep([\kicks, \bassdrum], Pseq([2,1], inf), inf),
	index: Pseq([1,3,2,5,4], inf),
	rate: 0.8,
	out: [2,30]);
Convenience.p(\time9, tempo: 154/60,
	dur: Pseq([Rest(3),1,1,1,1,1,3,1,3], inf)/Pseq([1,2,3], inf),
	// /2*Psinen(0.03).linlin(0.0,1.0,0.9,1.0),
	stretch: 1.0,
	amp: 0.2,
	pan: Pwhite(-0.2,0.2), pos: 0.0,
	folder: /*\Snaredrums */ \Wav,
	index: Pseq([/*42*/13,14,15,16,43,54], inf),
	pos: 0.0,
	rate: 1.0, //1.8
	pitchShiftOnOff: 1,
	pitchRatio: 4.0,
	formantRatio: 1.6,
	sustain: Pwhite(0.1,0.8),
	release: Pwhite(0.05,2.0),
	out: [2,30]);
Convenience.p(\time2, tempo: 154/60, dur: Prand([1,3,5,2,5], inf)/4,
	amp: Pfunc({arg envir; if(envir.folder == '\Roland_TR_707',{0.2},{0.4})}),
	pan: Pwhite(-0.3,0.3), pos: 0.0,
	folder: Pstep([\hihats, \Roland_TR_707], Pseq([5,1], inf), inf), index: Pseq([1,3,2,5,4], inf),
	rate: Pfunc({arg envir; if(envir.folder == '\Roland_TR_707',{0.6},{1.2})}),
	pitchShiftOnOff: 1,
	pitchRatio: 2.0,
	formantRatio: 2.6,
	out: [2,30]);
Convenience.p(\time3, tempo: 154/60,
	dur: 1,
	stretch: Pwhite(0.98,1.02), amp: 1.0, pan: Pwhite(-0.3,0.3), pos: 0.0,
	folder: '03_Hats', index: 7,
	rate: 2.0,
	out: [2,30]);
)

Convenience.modul(\continuum, [30,31], 16, [2,3])
Convenience.lfos(20, 0.01)
Convenience.map(\continuum)



(
~kickseq = Pseq([Rest(5),2,2,2,2,2,3,5,2,5,Rest(12),1], inf)/4;
Convenience.p(\time4, tempo: 154/60,
	dur: ~kickseq,
	amp: 1.4, pan: Pwhite(0.1,0.8), pos: 0.0,
	folder: \Bassdrums, index: 18,
	rate: 1.2,
	out: 2);
Convenience.p(\time5, tempo: 154/60,
	dur: ~kickseq,
	amp: 1.2, pan: Pwhite(-0.8,-0.1), pos: 0.0,
	folder: \Bassdrums, index: 19,
	rate: 1,
	release: 2.0,
	out: 2);
Convenience.p(\time6, tempo: 154/60,
	dur: ~kickseq,
	amp: 1.9,
	folder: \SP_SUBKIKS, index: 4,
	rate: 0.3,
	release: 3.0,
	out: 2);
Convenience.p(\time7, tempo: 154/60,
	dur: ~kickseq,
	amp: 0.3,
	folder: \classic_808, index: 19,
	pitchShiftOnOff: 1,
	pitchRatio: 2.2,
	formantRatio: 0.3,
	rate: 0.3,
	release: 2.0,
	out: 2);
Convenience.p(\time8, tempo: 154/60,
	dur: Pseq([Rest(5),1,3,5,2,5,Rest(12)], inf)/3,
	amp: 0.4,
	folder: \classic_808, index: 28,
	pitchShiftOnOff: 0,
	pitchRatio: 1.2,
	formantRatio: 0.8,
	rate: 2,
	release: 2.0,
	out: 2);
Convenience.p(\time1, tempo: 154/60,
	dur: Pseq([1,1,2,2,3,1,Rest(5)], inf)/2*0.5,
	stretch: Pwhite(0.99,1.01),
	amp: 0.9, pan: Pwhite(-0.3,0.3), pos: 0.0,
	folder: Pstep([\kicks, \bassdrum], Pseq([2,1], inf), inf),
	index: Pseq([1,3,2,5,4], inf),
	rate: 0.8,
	out: [2,30]);
Convenience.p(\time9, tempo: 154/60,
	dur: Pseq([Rest(6),1,1,1,1,1,3,1,3], inf)/Pseq([1,2,3], inf),
	// /2*Psinen(0.03).linlin(0.0,1.0,0.9,1.0),
	stretch: 1.0,
	amp: 0.2,
	pan: Pwhite(-0.2,0.2), pos: 0.0,
	folder: /*\Snaredrums */ \Wav,
	index: Pseq([/*42*/13,14,15,16,43,54], inf),
	pos: 0.0,
	rate: 1.0, //1.8
	pitchShiftOnOff: 1,
	pitchRatio: 3.0,
	formantRatio: 1.8,
	sustain: Pwhite(0.1,0.8),
	release: Pwhite(0.05,2.0),
	out: [2,30]);
Convenience.p(\time2, tempo: 154/60, dur: Prand([1,3,5,2,5], inf)/3,
	amp: Pfunc({arg envir; if(envir.folder == '\Roland_TR_707',{0.2},{0.4})}),
	pan: Pwhite(-0.3,0.3), pos: 0.0,
	folder: Pstep([\hihats, \Roland_TR_707], Pseq([5,1], inf), inf), index: Pseq([1,3,2,5,4], inf),
	rate: Pfunc({arg envir; if(envir.folder == '\Roland_TR_707',{0.6},{1.2})}),
	pitchShiftOnOff: 1,
	pitchRatio: 2.0,
	formantRatio: 3.6,
	out: [2,30]);
Convenience.p(\time3, tempo: 154/60,
	dur: 1,
	stretch: Pwhite(0.98,1.02), amp: 0.6, pan: Pwhite(-0.3,0.3), pos: 0.0,
	folder: '03_Hats', index: 8,
	rate: 3.0,
	out: [2,30]);
)