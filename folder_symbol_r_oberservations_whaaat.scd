C.crawl("/home/salkin/soundfiles_lenovo/pang");
C.crawl("~/soundfiles_lenovo/Drums");
C.crawl("~/soundfiles_lenovo/fra_ali/");
C.crawl("~/soundfiles_lenovo/rest/");
C.crawl("~/soundfiles_lenovo/ZINK", force: true);
C.crawl("~/SoundFiles/wav_files_found_in_old_sc_bin_folders")
C.crawl("~/SoundFiles/wav_files_found_in_old_sc_bin_folders", force: true)
C.crawl("/home/salkin/.local/share/SuperCollider/Recordings/Convenient_Recordings")
C.list

C.memoryUsage
C.memoryUsageMb

C.get('skogen_perc', 2)

C.record(\test, ~outer)
C.stopRecording

~outer = Bus.audio(s, 2)
~outer.numChannels
Bus.numChannels
0.class

Ndef(\listen, {In.ar(~outer, 2)}).play

C.pb(\ye, C.get("Convenient_Recordings", 3), rate: 0.5, pos: 0.4, loop:1, amp: 0.5)
C.sb(\ye, 0.4)
C.pb(\yey, C.get("Convenient_Recordings", 2), rate: 1.0, pos: 0.3, loop:1, amp: 0.5)
C.sb(\yey, 0.5)

C.s('c')
C.s('c', 1)
(
C.p('c',
    // bus: ~outer,
    bus: 0,
    seed: 890.rand.postln,
    numChannels: 2,
    // folder: 'dod',
    folder: 'Convenient_Recordings',
    // folder: 'pang',
    // folder: 'fra_ali',
    // folder: "hvad", // string will kill
    // folder: "Rest", //whaat
    // folder: 'ZINK',
    index: 4,
    // index: Pdup(Pdup(Pwhite(10,40), Pwhite(1,20)), Pwhite(4,6)),
    dur: Pwhite(0.005,0.1),
    stretch: 1.0,
    pos: PSinOsc(1.0008, 0.2).linlin(-1,1,0,1),
    rate: 0.1,//Pwhite(-1),
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
    pst: 1,
    pr: 0.2,
    fr: 20.0
)
);

