

C.crawl("~/soundfiles lenovo/Drums");
C.crawl("~/soundfiles lenovo/fra_ali/");
C.crawl("~/soundfiles lenovo/rest/");
C.crawl("~/soundfiles lenovo/ZINK");
C.crawl("~/SoundFiles/wav_files_found_in_old_sc_bin_folders")
C.crawl("~/SoundFiles/wav_files_found_in_old_sc_bin_folders", force: true)
C.list

C.get('skogen_perc', 2)

C.record(\test, ~outer)
C.stopRecording

~outer = Bus.audio(s, 2)
~outer.numChannels
Bus.numChannels
0.class

Ndef(\listen, {In.ar(~outer, 2)}).play

C.s('c')
(
C.p('c',
	bus: ~outer,
    seed: 890.rand.postln,
	numChannels: 2,
	// folder: 'dod',
	// folder: 'fra_ali',
	// folder: "hvad", // string will kill
	// folder: "Rest", //whaat
	// folder: 'ZINK',
	index: Pdup(Pdup(Pwhite(10,40), Pwhite(1,20)), Pwhite(4,6)),
	dur: Pwhite(0.005,0.1),
	stretch: 1.0,
	pos: PSinOsc(1.00008, 0.7).linlin(-1,1,0,1),
	rate: 1.1,//Pwhite(-1),
	tuningOnOff: 0,
	degree: Pseq([[1,5,9]]).repeat,
	octave: rrand(4,7),
	root: 0,
	scale: nil,
	cutoff: 22000.0,
	res: 0.01,
	fgain: 1.0,
	ftype: 0,
	bass: 1,
	pan: Pwhite(-1),
	width: 2.0,
	spread: 0.5,
	amp: 0.2,
	attack: PLFSaw(1.004).abs,
	sustain: PLFSaw(1.004).linlin(-1,1,0.1,2.0),
	release: PLFSaw(1.004, 0.5).linlin(-1,1,0.1,2.0),
	pst: 0,
	pr: 1.0,
	fr: 1.0
)
);

