

C.crawl("~/soundfiles lenovo/Drums");
C.crawl("~/soundfiles lenovo/fra_ali/");
C.crawl("~/soundfiles lenovo/rest/");
C.crawl("~/soundfiles lenovo/ZINK");
C.list

C.get('skogen_perc', 2)

C.s('c')
(
C.p('c',
	bus: 0,
    seed: 890.rand.postln,
	numChannels: 2,
	// folder: 'dod',
	// folder: 'fra_ali',
	// folder: "hvad", // string will kill
	// folder: "Rest", //whaat
	// folder: 'ZINK',
	index: Pwhite(1,10),
	dur: 0.01, //Pwhite(0.01,0.3),
	stretch: 1.0,
	pos: Psinen(0.05),
	rate: 6.1,//Pwhite(-1),
	tuningOnOff: 1,
	degree: Pseq([[1,5,9]]).repeat,
	octave: rrand(4,7),
	root: 0,
	scale: nil,
	cutoff: 22000.0,
	res: 0.01,
	fgain: 1.0,
	ftype: 0,
	bass: 0,
	pan: Pwhite(-1),
	width: 2.0,
	spread: 0.5,
	amp: 0.2,
	attack: 0.01,
	sustain: 1.0,
	release: 0.5,
	pst: 0,
	pr: 1.0,
	fr: 1.0
)
);

