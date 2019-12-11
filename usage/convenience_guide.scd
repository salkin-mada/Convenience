
// crawler, load folders in its way
Convenience.crawl
Convenience.crawl("/blabla/bbla", depth: 2)

//minimum
Convenience.p(\lay); // play first file in first folder
Convenience.s(\lay) // stop


// simple
(
Convenience.p(\lay,
	tempo: 8.0,
	out: 0,  // all Convenience synths outputs stereo, 2 channels
	index: 755,
	dur: Pseq([1,3,Rest(2),2], inf),
	rate: Pwhite(0.9,1.0)*Pstep([0.4,0.9,0.2]*2, 3, inf),
	pan: Pwhite(-0.15,0.15),
	amp: 0.2
);
Convenience.p(\lot,
	tempo: 8.0,
	out: 0,
	index: Pseq([130,160,160], inf),
	dur: Pseq([1,3,Rest(3),2], inf),
	rate: Pwhite(0.9,1.0)*Pstep([0.4,0.9,0.2]*2, 3, inf),
	pan: Pwhite(-0.15,0.15),
	attack: 0.01+Pwhite(0.0,0.04),
	amp: 0.9
);
Convenience.p(\lan,
	tempo: 8.0,
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
Convenience.s(\lay); Convenience.s(\lot); Convenience.s(\lan);

//Use Convenience just as a Buffer Class
play{PlayBuf.ar(1, Convenience.get(\sounds, 1), 0.41)}

// free a random folder
Convenience.free(Convenience.randomFolder)

// more sequencing examples
// dependency Else  --> Quarks.install("https://github.com/madskjeldgaard/Else")
(
Convenience.p(\lay,
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
Convenience.s(\lay)

// extended functionality example
// play
(
Convenience.p(\lay,
	tuningOnOff: Pstep([0,1],32,inf),
	fftOnOff: 1,
	tempo: 180/60*4,
	out: 0,
	dur: Pseq([1,3,2], inf)*Psine(0.02).linlin(-1.0,1.0,0.5,1.5),
	stretch: 1.0,
	folder: Pfunc({Convenience.randomFolder}).trace(prefix:"folder= "),
	index: Prand((0..100), inf),
	rate: Pwhite(0.98,1.0)*Pstep([1,2,3], 3, inf),
	degree: Pstep([1,3,5], 3, inf),
	octave: Pxrand([4,5], inf),
	root: 0,
	scale: Scale.minor,
	attack: 0.1,
	decay: 0.8,
	sustain: 1.0,
	release: 0.5,
	pan: Pwhite(-0.3,0.3),
	amp: 0.8,
	binRange: Pfunc({[Convenience.buckets(frame: ~frame, numBands: 20, band: 20.linrand)]})
);
)
// slay
Convenience.s(\lay)


// pitchshift
// small note, for now pitchShiftOnOff rules over fftOnOff
(
z.p(\ps,
	tempo: 145/60*4,
	tuningOnOff: 1,
	degree: Pseq([1,4,6,9], inf),
	octave: Pseq([5,6,7], inf),
	pitchShiftOnOff: 1,
	pitchRatio: Pwhite(0.2,0.5),
	formantRatio: Pwhite(0.8,5.2),
	folder: z.randomFolder.postln;,
	index: Pwhite(1,100).floor,
	cutoff: Psrp(0.01623).linexp(-1.0,1.0,100,1e3),
	pos: Pwhite(0.2,0.7),
	dur: Pseq([1,1,1,1,1,1,Rest(3),1,1,1,Rest(5)], inf),
	rate: Pwhite(0.9,1.0) * Pstep([0.4,0.9,0.2], Prand([3,5,8], inf), inf)+3,
	pan: Pwhite(-0.65,0.65),
	amp: 0.4,
	release: 7.0
);
)

z.s(\ps);
































// personal different things tester
////////////////////////////////////////////////////
ZzzZzZzzzZzz.randomFolder
Convenience.folderPaths.keysDo{|i|i.postln}
Convenience.folderPaths.size
Convenience.clearFolderPathsDict
Convenience.buffers.keysDo{|key|key.postln}
Convenience.size
Convenience.folders
Convenience.folders.size
Convenience.folders.do{|item| item.postln}

Convenience.free(\someFolder)
Convenience.free // free all

Convenience.list.size
Convenience.p(\playos, pan: Psrp(0.3234), dur: 0.15, folder: Pfunc({Convenience.folderNum(10.rand)}).trace)

Convenience.crawl("/some/folder", depth: 5);
Convenience.crawl; // open dialog
Convenience.buffers.keysDo{|item| item.postln}
Convenience.folders

Convenience.dir
Convenience.folderPaths.keys
Convenience.folders.collect{|item| item.postln}
Convenience.folders.size
Convenience.buffers
Convenience.get(\someFolder, 2).path
Convenience.get(\someFolder, 4)

