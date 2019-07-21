Salk.loadFolders("X:/lydfiler/mona sigler generate spasm/YAMAHA");
Salk.folders
Salk.get(\tg500_fx, 2).path

//minimum
Salk.p(\lay); // play first file in first folder
Salk.s(\lay) // stop


// simple
(
Salk.p(\lay,
	tempo: 140,
	out: 0,  // all Salk synths outputs stereo, 2 channels
	index: 755,
	dur: Pseq([1,3,Rest(2),2], inf),
	rate: Pwhite(0.9,1.0)*Pstep([0.4,0.9,0.2]*2, 3, inf),
	pan: Pwhite(-0.15,0.15),
	amp: 0.2
);
Salk.p(\lot,
	tempo: 140,
	out: 0,
	index: Pseq([130,160,160], inf),
	dur: Pseq([1,3,Rest(3),2], inf),
	rate: Pwhite(0.9,1.0)*Pstep([0.4,0.9,0.2]*2, 3, inf),
	pan: Pwhite(-0.15,0.15),
	attack: 0.01+Pwhite(0.0,0.04),
	amp: 0.9
);
Salk.p(\lan,
	tempo: 140,
	out: 0,
	index: Pseq([160,162], inf),
	dur: Pseq([1,3,Rest(4),2], inf),
	rate: Pwhite(0.9,1.0)*Pstep([0.4,0.9,0.2], 3, inf),
	pan: Pwhite(-0.15,0.15),
	attack: 0.01+Pwhite(0.0,0.04),
	amp: 0.4
);
)
// stop
Salk.s(\lay); Salk.s(\lot); Salk.s(\lan);

// more
(
Salk.p(\lay,
	tuningOnOff: 1,
	out: 0,
	dur: Pseq([1,3,2], inf)*Psine(0.2).linlin(-1.0,1.0,0.1,1),
	stretch: 2.0,
	folder: Pseq([\tg500_fx], inf),
	index: Pseq([4,99,80,108,109], inf),
	//rate: Pwhite(0.98,1.0)*Pstep([0.4,0.9,0.2], 3, inf),
	degree: Pstep([1,3], 16, inf),
	octave: Pxrand([4,6,7], inf),
	attack: 0.1,
	decay: 0.8,
	sustain: 1.0,
	release: 8.5,
	pan: Pwhite(-1.0,1.0),
	amp: 0.3
);
)
Salk.s(\lay)

// all functionality example
Salk.loadFolders("X:/lydfiler/mona sigler generate spasm/YAMAHA");
// play
(
Salk.p(\lay,
	tuningOnOff: 1,
	fftOnOff: 1,
	out: 0,
	dur: Pseq([1,3,2], inf)*Psine(0.2).linlin(-1.0,1.0,0.1,1),
	stretch: 2.4,
	folder: Pseq([\tg500_fx], inf),
	index: Pseq([2], inf),
	//rate: Pwhite(0.98,1.0)*Pstep([0.4,0.9,0.2], 3, inf),
	degree: Pstep([1,3,5], 3, inf),
	octave: Pxrand([4,5], inf),
	root: 0,
	scale: Scale.chromatic,
	attack: 0.1,
	decay: 0.8,
	sustain: 1.0,
	release: 0.5,
	pan: Pwhite(-0.3,0.3),
	amp: 0.3,
	binRange: Pfunc({[Salk.buckets(frame: ~frame, numBands: 20, band: 20.linrand)]})
);
)
// slay
Salk.s(\lay)

