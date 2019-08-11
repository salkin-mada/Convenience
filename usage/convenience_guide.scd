
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

// more
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

// all functionality example
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




















////////////////////////////////////////////////////
Convenience.randomFolder
ZzzZzZzzzZzz.randomFolder
Convenience.folderPaths.keysDo{|i|i.postln}
Convenience.folderPaths.size
Convenience.clearFolderPathsDict
Convenience.buffers.keysDo{|key|key.postln}
Convenience.size
Convenience.folders
Convenience.folders.size
Convenience.folders.do{|item| item.postln}
Convenience.free(Convenience.randomFolder)
Convenience.free(\bats)
Convenience.free(\hurlumhej)
Convenience.free(\kirke_orgel)
Convenience.free(\snappi)
Convenience.free(\wood_blocks)
Convenience.free(\trylleri)
Convenience.free // free all

Convenience.list.size
Convenience.p(\yap, pan: Psrp(0.3234), dur: 0.15, folder: Pfunc({Convenience.folderNum(10.rand)}).trace)

(
Convenience.p(\yap,
	pan: Psrp(0.3234),
	dur: 0.15*Prand((1..5), inf),
	stretch: Psine(0.05).linlin(-1.0,1.0,0.2,1.0),
	folder: Pfunc({Convenience.randomFolder}).trace(prefix:"folder= "),
	index: Prand((0..100), inf),
	//pos: Prand((0..40)/100, inf),
	rate: 1.8,
	amp: 0.4,
	sustain: 0.2,
	release: 0.01+Prand((0..20)/10, inf));
)
Convenience.s(\yap);
(
Convenience.p(\sup,
	pan: Psrp(0.3234),
	dur: 0.15*Prand((1..5), inf),
	stretch: Psine(0.05).linlin(-1.0,1.0,0.2,1.0),
	folder: Convenience.folderNum(18).postln,
	index: Prand((0..100), inf),
	rate: 1.8,
	amp: 0.7,
	sustain: 0.7,
	release: 0.01+Prand((0..20)/10, inf))
)
Convenience.s(\sup)

Convenience.crawl("/blabla/bbla", depth: 9);
Convenience.crawl; // open dialog

Convenience.crawl("/blabla/bbla", depth: 3)
Convenience.buffers.keysDo{|item| item.postln}
Convenience.folders

Convenience.dir
Convenience.folderPaths.keys
Convenience.folders.collect{|item| item.postln}
Convenience.folders.size
Convenience.buffers
Convenience.get(\tg500_fx, 2).path
Convenience.get(\test, 4)

play{PlayBuf.ar(1, Convenience.get(\test, 4), 0.01)}
