

C.record(\input, [22,23]);
C.stopRecording;

C.crawl(Platform.recordingsDir++"/CR");
C.list;

C.s('æøå', 3)
(
C.p('æøå',
    numChannels: 3,
    bus: 0, // danger!! > [0,1],
    folder: C.folderNum(0).postln,
    index: 5,
    dur: Pseg([0.1,0.2], rrand(1,10), rrand(-10,10), inf),
    pos: Pseg([0.0,1.0], 60, rrand(-10,10), inf),
    rate: Pseq([[1/2,1,2]],inf)*0.5,
    // rate: Ndef('a', {1+SinOsc.kr(0.3).abs*3}),
    amp: Pseg([0.4,1.0], rrand(1,10), rrand(-10,10), inf)*0.3,
    attack: 0.2,
    sustain: 0.1,
    release: 0.2,
    // pan: Pwhite(-1),
    pan: Pstep([0.0,0.25,0.5,0.75,1.0],1, inf).trace,
    width: 1.0,
    spread: 1.0
)
)
C.bus('æøå').scope;

C.s('umlaut', 3)
(
C.p('umlaut',
    bus: 0,
    folder: C.folderNum(0).postln,
    index: 0,
    dur: Pseg([0.1,0.2], rrand(1,10), rrand(-10,10), inf),
    pos: Pseg([0.0,1.0], 60, rrand(-10,10), inf),
    rate: Pseq([[1/2,1,2]],inf),
    amp: Pseg([0.4,1.0], rrand(1,10), rrand(-10,10), inf)*0.3,
    attack: 0.2,
    sustain: 0.1,
    release: 0.2,
    pan: Pwhite(-1)
)
)

C.bus('umlaut').scope

Node

Ndef('umlaut').group.nodeID
Ndef('umlaut').group.inspect
Ndef('umlaut').group.dumpTree

s.queryAllNodes

C.bus('æøå').scope

C.record(\ooooo, C.bus('æøå'))
C.record(\ooooo, 'æøå')
C.stopRecording
C.record(\ooo, [0,1])

C.pb(\yo, C.get(\CR, 3))
C.sb('yo')




C.crawl(Platform.recordingsDir++"/CR/")
C.free
C.list


(
Ndef('yaw', {
    var sig = SinOsc.ar(LFNoise1.kr(2).linlin(-1,1,400,8000)) * 0.1;
    Pan2.ar(sig, LFNoise1.kr(1))
}).playN([0,1])
)

C.record(\ooo, Ndef('yaw').bus)
C.stopRecording

Recording Stopped: (ooo_230221_115559.wav)
C.free(\CR)
C.crawl(Platform.recordingsDir++"/CR")
C.list
C.pb(\yo, C.get(\CR, 1), rate: 1, amp: 0.4)
C.pb(\yo, C.get(\CR, 1), rate: Ndef('nejnejnejnej', { |freq=1.7| LFSaw.ar(freq * SinOsc.ar(3.86)).linlin(-1,1,-4,4)}), amp: 0.4)
C.sb(\yo)
C.record('yoo', Ndef('yo').bus)
C.stopRecording

C.sall

s.plotTree

Ndef.all.do{|n| n.clear}


(
Ndef('papaya', { |freq=2846.7| LFSaw.ar(freq * SinOsc.ar(20.86*LFNoise1.kr(1).abs)) !2 * 0.3}).mold(2).play;
)
Ndef('papaya').stop
Ndef('papaya').bus



(
SynthDef(\fungerer, { |out|
    Out.ar(out,
        SinOsc.ar(Rand(400, 800), 0, 0.2) * Line.kr(1, 0, 1, doneAction: Done.freeSelf) ! 2
    )
}).add;
SynthDef(\satan, {
    |
    bufnum, out, loop = 0, rate = 1, pan = 0, spread = 0.5, width = 2, amp = 0.5,
    attack = 0.01, sustain = 0.5, release = 1.0, pos = 0,
    gate = 1, cutoff = 22e3, res = 0.01, fgain = 1.0, ftype = 0, bass = 0.0
    |
    var sig, key, frames, env, file;
    frames = BufFrames.kr(bufnum);
    sig = ConvenientBufferPlayer.ar(
        2,
        bufnum,
        rate*BufRateScale.kr(bufnum),
        1,
        pos*frames,
        loop: loop
    );
    env = EnvGen.ar(Env.linen(attack, sustain, release), gate);
    FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
    sig = DFM1.ar(in: sig, freq: cutoff, res: res, inputgain: fgain, type: ftype);
    sig = sig + (LPF.ar(sig, 100, bass));
    sig = SplayAz.ar(2, sig, spread: spread, width: width, center: pan, level: amp);
    sig = LeakDC.ar(sig);
    Out.ar(out, (sig*env));
}).add;
Pdef('lol', 
    Pseed(666, Pbind(*[
        instrument: \satan,
        // instrument: \fungerer,
        // bufnum: C.get()
    ]
))
); // lol
)
(
Ndef('lol').source = Pdef('lol');
// Ndef('lol').reshaping = \elastic;
// Pdef('lol').quant_(0);
// Ndef('lol').quant_(0);
fork{
    Ndef('lol').play(out: 0);
    Server.default.sync;
    Server.default.reorder([Ndef('lol')], Group(Server.default));
})

Ndef('lol').stop(2)
Pdef('lol').clear
Ndef('lol').clear
Ndef('lol').bus.scope

C.record('lol', Ndef('lol').bus)
C.stopRecording


Ndef('umlaut').group.nodeID
Ndef('umlaut').group.inspect
Ndef('umlaut').group.dumpTree

s.queryAllNodes





C.free
C.crawl("~/Reaper/Projects/nams_-_field_init/")
C.list
C.s('c')
(
C.p('c',
    folder: C.folderNum(0).postln,
    index: 3,
    dur: Pseg([0.05,0.8,0.05], 10, 0, inf),
    pos: Pseg([0.40,0.74], 200, 0, inf),
    rate: 1+Pseq([[1/4,1]],inf)*Pseg([0.99,1.01], 3, -2, inf),
    amp: 0.8*Pseg([0.8,1.0], 3, -8, inf),
    bass: 2,
    attack: 0.2,
    sustain: 0.5,
    release: 0.2,
    pan: Pwhite()
)
)

C.bus('c')
C.record('ccc', 'c')
C.stopRecording


C.crawl("~/Sounds",1)
C.list
C.s('c')
(
C.p('c',
    // folder: 'mikrohunder',
    index: 6,
    dur: Pseg([0.05,0.2,0.05], 10, 0, inf),
    pos: Pseg([0.02,0.85], 15, 0, inf),
    rate: 2+Pseq([[1/4,1]],inf)*Pseg([0.99,1.01], 3.5, -2, inf),
    amp: 0.7*Pseg([0.8,1.0], 3, -8, inf),
    bass: 2,
    attack: 0.2,
    sustain: 0.5,
    release: 0.2,
    pan: Pwhite()
)
)
C.pp('c', 'index',3)
C.pp('c', 'index',Pseg([0.80,0.85], 25, 0, inf))
C.record('ccc_insekt', 'c')
C.stopRecording


C.record('satan', [22,23])
C.stopRecording
C.crawl("~/soundfiles_lenovo/lithops + fant eeled early mix",2)
C.crawl("~/.local/share/SuperCollider/Recordings/CR/hallo_230305_111333.wav")
C.list
C.memoryUsageMb
C.free
C.crawl("~/.local/share/SuperCollider/Recordings/CR/hallop22_230305_130951.flac")
C.crawl("~/.local/share/SuperCollider/Recordings/CR/hallop22_230305_132200.flac")

(
C.p('cccc',
    folder: C.folderNum(0).postln,
    index: 1,
    dur: Pseg([0.09,0.23], rrand(1,10), rrand(-10,10), inf),
    pos: Pseg([0.1,1.0], rrand(1,10), rrand(-10,10), inf),
    rate: Pseg([-1.0,1.0], rrand(1,10), rrand(-10,10), inf),
    amp: Pseg([0.4,0.5], rrand(1,10), rrand(-10,10), inf),
)
)
C.s('cccc');
C.repeat('cccc', 1, 5, inf);
C.record('hallop22', 'cccc', format: "yoo");
C.record('hallop22', 'cccc', format: "flac");
C.stopRecording;

// TODO(salkin-mada)
C.pp('cccc', \bus, 0) // bug??

Ndef(\yeah, {SinOsc.ar([1002,2002])})
Ndef(\yeah).numChannels
C.crawl
C.crawl("~/soundfiles_lenovo/lithops + fant eeled early mix/lithops.wav")
C.list
C.free

C.buffers['CFiles'].size

Convenience.crawl

d = Dictionary.new()
10.do{d.add("yeahs".scramble.asSymbol -> 123.rand)}; d.keysValuesDo{|k,v|"% %".format(k,v).postln};d.size;
d.size
d.any{ |item| item == 68}.not
d.clear


(
C.p('sdf',
    folder: C.folderNum(0).postln,
    index: 1,
    dur: Pseg([0.001,0.01], rrand(1,10), rrand(-10,10), inf),
    pos: Pseg([0.0,1.0], 5000, 0, inf),
    rate: Pseg([3.0,1/4],1,-10,inf),
    amp: 0.4,
    attack: 0.001,
    sustain:0.03,
    release:0.050
)
)

C.rec()
