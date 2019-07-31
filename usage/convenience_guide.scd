Salk.crawl
Salk.crawl("/home/na", 6)

Salk.randomFolder
ZzzZzZzzzZzz.randomFolder
Salk.folderPaths.keysDo{|i|i.postln}
Salk.folderPaths.size
Salk.clearFolderPathsDict

// efter boot
Salk.buffers.keysDo{|key|key.postln}
Salk.size

Salk.folders
Salk.folders.size
Salk.folders.do{|item| item.postln}
Salk.free(Salk.randomFolder)
Salk.free(\bats)
Salk.free(\hurlumhej)
Salk.free(\kirke_orgel)
Salk.free(\snappi)
Salk.free(\wood_blocks)
Salk.free(\trylleri)
Salk.free // free all

Salk.list.size

Salk.p(\yap, pan: Psrp(0.3234), dur: 0.15, folder: Pfunc({Salk.folderNum(10.rand)}).trace)


(
Salk.p(\yap,
	pan: Psrp(0.3234),
	dur: 0.15*Prand((1..5), inf),
	stretch: Psine(0.05).linlin(-1.0,1.0,0.2,1.0),
	folder: Pfunc({Salk.randomFolder}).trace(prefix:"folder= "),
	index: Prand((0..100), inf),
	//pos: Prand((0..40)/100, inf),
	rate: 1.8,
	amp: 0.4,
	sustain: 0.2,
	release: 0.01+Prand((0..20)/10, inf));
)
Salk.s(\yap);
(
Salk.p(\sup,
	pan: Psrp(0.3234),
	dur: 0.15*Prand((1..5), inf),
	stretch: Psine(0.05).linlin(-1.0,1.0,0.2,1.0),
	folder: Salk.folderNum(18).postln,
	index: Prand((0..100), inf),
	rate: 1.8,
	amp: 0.7,
	sustain: 0.7,
	release: 0.01+Prand((0..20)/10, inf))
)
Salk.s(\sup)

Salk.crawl("/home/na", depth: 9);
Salk.crawl; // open dialog

Salk.crawl("/home/na/Desktop/forSalk/test", depth: 3)
Salk.buffers.keysDo{|item| item.postln}
Salk.folders
Salk.buffers.at(\test)
Salk.buffers.at(\folder2).size

// new name idea
Convenient.crawl
Lazy.crawl
// pseudonym , shorhand shift+zz enter !
ZzzZzZzzzZzz.crawl

Salk.crawl("/home/na/Desktop/forSalk", depth: 1);
Salk.load("/home/na/Desktop/forSalk/test");
Salk.dir
Salk.prFreeBuffers
Salk.folderPaths.keys
Salk.folders.collect{|item| item.postln}
Salk.folders.size
Salk.buffers
Salk.get(\tg500_fx, 2).path
Salk.get(\test, 4)

play{PlayBuf.ar(1, Salk.get(\test, 4), 0.01)}


//minimum
Salk.p(\lay); // play first file in first folder
Salk.s(\lay) // stop


// simple
(
Salk.p(\lay,
	tempo: 8.0,
	out: 0,  // all Salk synths outputs stereo, 2 channels
	index: 755,
	dur: Pseq([1,3,Rest(2),2], inf),
	rate: Pwhite(0.9,1.0)*Pstep([0.4,0.9,0.2]*2, 3, inf),
	pan: Pwhite(-0.15,0.15),
	amp: 0.2
);
Salk.p(\lot,
	tempo: 8.0,
	out: 0,
	index: Pseq([130,160,160], inf),
	dur: Pseq([1,3,Rest(3),2], inf),
	rate: Pwhite(0.9,1.0)*Pstep([0.4,0.9,0.2]*2, 3, inf),
	pan: Pwhite(-0.15,0.15),
	attack: 0.01+Pwhite(0.0,0.04),
	amp: 0.9
);
Salk.p(\lan,
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
// play
(
Salk.p(\lay,
	tuningOnOff: Pstep([0,1],32,inf),
	fftOnOff: 1,
	tempo: 180/60*4,
	out: 0,
	dur: Pseq([1,3,2], inf)*Psine(0.02).linlin(-1.0,1.0,0.5,1.5),
	stretch: 1.0,
	folder: Pfunc({Salk.randomFolder}).trace(prefix:"folder= "),
	index: Prand((0..100), inf),
	rate: Pwhite(0.98,1.0)*Pstep([1,2,3], 3, inf),
	degree: Pstep([1,3,5], 3, inf),
	octave: Pxrand([4,5], inf),
	root: 0,
	scale: Scale.chromatic,
	attack: 0.1,
	decay: 0.8,
	sustain: 1.0,
	release: 0.5,
	pan: Pwhite(-0.3,0.3),
	amp: 0.8,
	binRange: Pfunc({[Salk.buckets(frame: ~frame, numBands: 20, band: 20.linrand)]})
);
)
// slay
Salk.s(\lay)

