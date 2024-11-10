C.crawl("~/soundfiles_lenovo/natur/ICEMELT.wav")
C.crawl("~/soundfiles_lenovo/natur/Uirapuru.wav")
C.list

(
b=(
    dur: Pseg([0.1,0.6], rrand(1,10), rrand(-10,10), inf),
    pos: Pseg([0.1,1.0], rrand(1,10), rrand(-10,10), inf),
    rate: Pwhite(1,3),
    ftype: 0,
    cutoff: 2e4,
    attack: 0.1,
    sustain: 0.1,
    release: 0.1,
);
a=(
    folder: C.folderNum(0).postln,
    index: 1,
    dur: Pseg([0.1,0.2], rrand(1,10), rrand(-10,10), inf),
    pos: Pseg([0.1,1.0], rrand(1,10), rrand(-10,10), inf),
    rate: 0.1,
    ftype: Pseq([0,1],inf),
    cutoff: Pseg([3000,16000],10,\lin,inf).trace,
    attack: 1.1,
    sustain: 1.1,
    release: 1.1,
    amp: 0.5,
    pan: Pwhite()
).blend(b);
C.p('æøå', a);
)
C.sall
s.latency_(0.3)

C.pp('æøå', \rate, 1);
C.pp('æøå', \dur, 0.01);
C.pp('æøå', \attack, 0.01);
C.pp('æøå', \release, 0.01);
C.pp('æøå', \ftype, Pstep([0,1],6,inf));
C.pp('æøå', \cutoff, Pseg([300,20000],3,\lin,inf));

b.class

C.p('æøå', Dictionary[\dur -> 0.2, \pos -> 0.4]);

// parent and proto variables ( tjek)

C.crawl("~/soundfiles_lenovo/Authentic Sound Effects Vol. 1")
C.list

(
Ndef(\filt, {SinOsc.kr(0.1).linexp(-1,1,200,8e3)});
C.p([ 
    name: 'yu',
    // quant: 4,
    seed: 234.rand,
    dur: 0.05,
    index: 48,
    sustain: 0.01,
    pos: Pseg([0,1], 6, 0, inf),
    satan: Pkey(\pos),
    attack: Prand([0.1,0.8],inf), //0.1,
    cutoff: Ndef(\filt),
    rate: Pseg([0.5,0.8],7,10,inf)*2,
    amp: 0.5
    // special: Pkey(\rate),
    // another: Pkey(\special).trace(prefix: "another: "),
]);
C.p([ 
    name: 'bui',
    seed: 234.rand,
    dur: 0.05,
    index: 52,
    sustain: 0.01,
    pos: Pseg([0,1], 12, 0, inf),
    attack: Prand([0.1,0.8],inf), //0.1,
    // cutoff: Ndef(\filt),
    rate: Pseg([0.8,0.9],7,10,inf)*3,
    amp: 0.4,
    // special: Pkey(\rate).trace,
    // another: Pkey(\special).trace(prefix: "another: "),
]);
)
C.crawl("~/soundfiles_lenovo/ZINK")
C.list
C.pattern_properties.keys.includes(\yu)
C.pattern_properties['yu'][\rate]
C.pattern_properties.includesKey(\yu)
C.patterns.includes(\yu)
s.latency_(0.5)

Pdefn(\bui_rate)
Pdefn(\bui_rate, 8)
Pdefn(\yu_rate, 8)

C.s('yu')
C.s('bui')
C.sall
C.patterns

Pdef.all

C.pp('bui', 'rate', rrand(1,3).postln)
C.pp('yu', 'rate', Pseq([3],inf))
C.pp('yu', 'rate', 1)
C.pp('bui', 'rate', 1)
C.pp('bui', 'special', 1234.rand)
C.pp('bui', 'special')
C.pp('yu', 'rate')
C.pp('yu', 'folder', C.folderNum(0).postln)
C.pp('yu', 'folder')
C.pp('yu', 'another', Pkey(\rate).trace)

(
Pdef('?', Pbind(*[
    dur: 0.2,
    degree: Pseq([1,2,4,6], inf),
    octave: 6,
    index: Prand((0..190).rand, inf),
    lol: (Pkey(\degree) / 2.2),
    print: Pkey(\lol),
    satan: (Pkey(\print)/0.4),
    what: (Pkey(\index)/0.9),
    hehej: Pdefn(\yu_seed).trace

])).play // ?
)









a = ().asDict;
a.putPairs([
    \dur, 0.1,
    \index, 1
]);

C.crawl("~/soundfiles_lenovo/ZINK")
C.list
C.crawl("~/soundfiles_lenovo/Authentic Sound Effects Vol. 1")

// single dict
(
C.p((
    name: 'bobo',
    seed: 12390.rand,
    index: Prand((10..18),inf)+58,
    dur: Pseg([0.05,0.1,0.05], 5, 6, inf),
    pos: Pseg([0.0,0.8], 8, -4, inf),
    rate: Pwhite(1,6),
    // yoyo: Pkey(\rate).trace,// FAIL
    // nej: Pkey(\yoyo).trace,// FAIL ??? HUUIUUHHHH
    cutoff: Pwhite(20,20e3),
    special: (Pkey(\cutoff)/8000).trace,
    attack: Pkey(\special)/8,
    sustain: 0.1,
    release: 0.001,
    amp: 0.4,
    // what: Pkey(\print)*2,
))
)
C.repeat('bobo', *[rrand(0,30),rrand(31,80)].postln)
C.repeat('bobo', 0, 20)
C.pp('bobo', \release, 3) // FAIL



// blend
(
a = (
    name: 'obo',
    // bus: 4.rand,
    folder: C.folderNum(0).postln,
    index: Prand((10..12),inf)+18,
    dur: Pseg([0.05,0.1,0.05], 5, 6, inf),
    pos: Pseg([0.0,0.8], 8, -4, inf),
    degree: Pseq((1..12),inf),
    rate: Pwhite(1,80),
    cutoff: Pwhite(20,20e3),
    print: (Pkey(\cutoff)/8000),
    attack: Pkey(\print),
    sustain: 0.1,
    release: 0.001,
    scale: Scale.mixolydian,
    amp: 0.2
);
b = (
    name: 'bo',
    replay: 1,
    seed: 12390.rand,
    // bus: 4.rand,
    folder: C.folderNum(1).postln,
    index: Prand((10..12),inf)+58,
    dur: Pseg([0.05,0.1,0.05], 5, 6, inf),
    pos: Pseg([0.0,0.8], 8, -4, inf),
    // rate: Pwhite(1,80),
    degree: Pseq((12..1),inf),
    cutoff: Pwhite(20,20e3),
    print: (Pkey(\cutoff)/8000),
    attack: Pkey(\print)/8,
    sustain: 0.1,
    release: 0.001,
    tuningOnOff: 1,
    amp: 0.2
    // scale: Scale.yu,
    // quant: 0,
    // tempo: 1.0
);
C.p(blend(a,b))
// C.p(a);
// C.p(b)
)
C.patterns

C.sall
C.properties




{|a = 10, b = 40| a / b}.def.sourceCode
{|a = 10, b = 40| a / b}.def.context
e= {|a = 10, b = 40, c| a/b}
e= {|a = 10, b = 40, c| a/b}.def.keyValuePairsFromArgs
e
f = e.collect{|n| if (n.isNil) {^\nil} {n}}.asDict

f = Dictionary.with(e.def.keyValuePairsFromArgs.collect{arg key, value; key -> value});
f

d = Dictionary.new()
d.add(\a->10)
d.add(\yo->\nil)

if (d[\a].isInteger) {d[\a] = d[\a].asString}
d[\a].isString

d[\d] = 2
r = d[\c] ? 10
r

f= Dictionary.with(e.collect{|n|n.postln});


e= {|a = 10, b = 40, c = \nil| a/b}.def.makeEnvirFromArgs
(
e.use {
    [\a, \b, \c, \lol].envirPairs;
}
)



// TODO: WOW!!
// !!!!  --->   softSet



(
var ass = (d: 3, c: Pseq((1..10), inf), scale: Scale.yu);
var bos = (c: Pseq((10..20), inf));
// blend(a,b)[\c].asStream.nextN(10);
g=merge(ass,bos,{|en,to| 
    en.postln;
    blend(a,b)
});
// g[\c].asStream.nextN(10)
)

u=Scale.yu
m=Scale.mixolydian
blend(u,m, func:{|j| j.postln})

d = Dictionary[\a -> 500, \b -> 0.001];
e = Dictionary[\a -> 300, \b -> 0.1];
blend(d, e, 0.3, specs: (a: \freq, b: \rq));





a = IdentityDictionary.new
a.put(\yeah, Dictionary.with(\yi->123, \what->666))

a[\yeah].keysDo


a = IdentitySet()
a.add(\asd)
a.includesKey(\asd)

C.properties.class

a = [\lol -> 123]
a.class
a.at(\lol)

a = Dictionary.new
a.add("lol" -> 99999.rand)
a.add("asldjapgfuqpevbv".scramble.asSymbol -> 99999.rand)
a.size

b = Dictionary.new
b.add('lol' -> 99999.rand)
b.add('yoyo' -> 99999.rand)
b.add("asldjapgfuqpevbv".scramble.asSymbol -> 99999.rand)
b.size

b.any()

c = a++b
c.size

d = b.reject{|k|k.postln; k=='lol' or: (k=='yoyo')}
d.size


a = C.pattern_properties[\yu].reject{|v,k|k=='quant' or: (k=='tempo')}.keysValuesDo{|k,v|"%: %".format(k,v).postln}
b = Dictionary.with(*[\x ->Pseq([1, 2, 3]), \y ->Prand([100, 300, 200], inf), \zzz ->99])
b.asKeyValuePairs.postln
(
a = Pbind().patternpairs_(b.asKeyValuePairs);
// a = Pbind(\x, Prand([0,3,2],inf));
x = a.asStream;
x.next(())
)

a = Pgeom(1, Pwhite(1.01, 1.2), inf);
b = a.asStream; c = a.asStream;

b.next;
b.next;
b.next;

b =OrderedIdentitySet.with(\x,Pseq([1, 2, 3]), \y,Prand([100, 300, 200], inf), \zzz,99)
c = b.collect{|i|i.postln}
c.class

b = OrderedIdentitySet.with(\x,Pseq([1, 2, 3]), \y,Prand([100, 300, 200], inf), \zzz,99)
b.asPairs

b = OrderedIdentitySet.with(*[\x ->Pseq([1, 2, 3]), \y ->Prand([100, 300, 200], inf), \zzz ->99])
c = b.collectAs({|assoc| assoc.key -> assoc.value}, Dictionary)
c[\zzz]

C.pattern_properties[\yu].value.do{|assoc|assoc.postln}
b.do{|assoc|"%: %".format(assoc.key, assoc.value).postln; [assoc.key, assoc.value]}

OrderedIdentitySet.with(*[\x ->Pseq([1, 2, 3]), \y ->Prand([100, 300, 200], inf), \zzz ->99]).collectAs({|assoc| assoc.key -> assoc.value}, Dictionary)

b.asDict
b.do{|o|o.asPairs.postln}

c = b.collect{|k,v| if (k=='x') {k=666}; k}
b.items
c.at(\x)

b.do{|p|p.postln}


b = OrderedIdentitySet.new
b.with[\x ->Pseq([1, 2, 3]), \y ->Prand([100, 300, 200], inf), \zzz ->99]
b
c = b.collectAs({|assoc| assoc.key -> assoc.value}, Dictionary)
c[\zzz]

// a = Dictionary.with(*[\x->Pseq([1, 2, 3]), \yo->Prand([100, 300, 200], inf), \zzz->99]).asAssociations;
a = Dictionary.with(*[\x->Pseq([1, 2, 3]), \yo->Prand([100, 300, 200], inf), \zzz->99]).asAssociations;
b = OrderedIdentitySet[\adasd -> 123, \yo -> 999];
(a++b).do{|assoc| assoc.postln};


C.pattern_properties[\yu].collectAs({|assoc| assoc.key -> assoc.value}, Dictionary)[\server]

C.ptrPropsAsDictionary(C.pattern_properties[\yu])[\cutoff]

x = [xoc: 'satan', degree: Pseq([0,1]), cutoff: Prand([4e4,8e4])]
x.asDict[\degree];
a = ConvenientOrderedIdentityDictionary[(\hej -> 123), \degree -> 0, \cutoff -> 12e4];
// b = ConvenientOrderedIdentityDictionary.newFromAssociationArray[x.asAssociations]
b = ConvenientOrderedIdentityDictionary.newFromAssociationArray(x.asAssociations)
c = a++b
c[\server] = 23



x = (xoc: 'satan', degree: Pseq([0,1]), cutoff: Prand([4e4,8e4]))
y = [(\hej -> 123), \degree -> 0, \cutoff -> 12e4].asDict;
a = ConvenientOrderedIdentityDictionary(x++y)
a.put(y++x)
c[\cutoff]


i = [ (\name -> 'yu'), (\type -> Convenience), (\dur -> 0.07), (\index -> 1), (\replay -> 1), (\sustain -> 0.1), (\rate -> 1), (\pos -> Pseq([0,1])), (\satan -> Pkey().trace), (\cutoff -> Pkey()) ]
i.asAssociations.class

a = ConvenientOrderedIdentityDictionary(i.asAssociations)

a = Dictionary()
a.put(\asd, 123)
a.put(\asd, nil)
a.removeAt(\asd)
a


(
Pdef(\asd, Pbind(*[
    dur: 1/4,
    degree: Pseq([0,4,7,9,3,5],inf),
    octave: Pdefn(\hej, 6),
])).play
)
Pdef(\asd).fadeTime_(5)
Pbindef(\asd, \octave, Pdefn(\hej, 4))
Pdefn(\hej, 3)




    // Pwhite repeats its three values forever
    Pseed(Pdefn(\seed), Pwhite(1, 10, 3)).asStream.nextN(10);
    Pdefn(\seed, 123)
    // Pwhite runs once:
    // the output stream consists of three values, then 'nil' ad infinitum
    Pseed(Pn(1000, 1), Pwhite(1, 10, 3)).asStream.nextN(10);

s.options.inDevice_("?");
s.options.outDevice_("?");
s.options.numInputBusChannels_(2);
s.options.numOutputBusChannels_(2);

s.options.sampleRate_(44100)

PathName("/Users/salkin/Music/Arne Nordheim - The Nordheim Tapes (2008) [FLAC]/CD 1/").isFolder;
PathName("/Users/salkin/Music/Arne Nordheim - The Nordheim Tapes (2008) \\[FLAC\\]/CD 1/").isFolder;
PathName("/Users/salkin/Music/Arne Nordheim - The Nordheim Tapes (2008)").isFolder;

C.crawl("~/Music/Arne Nordheim - The Nordheim Tapes (2008) [FLAC]/CD 1/", 1, true)
C.crawl("~/Music/Arne Nordheim - The Nordheim Tapes (2008) [FLAC]/CD 1/".asAbsolutePath, 1, true)
C.crawl("~/Sounds/tofuya.wav")
C.crawl("/Users/salkin/Sounds/pusten", force: true)
C.size
C.get('CFiles',0)
C.crawl
C.crawl(force:true)
C.buffers
C.files
C.folderPaths
C.get(1,2).play
"/lol\\".tr(Platform.pathSeparator , $/)
C.get(2,3).play
C.list

C.loadFn.(s)
C.crawl("/Users/salkin/Sounds/pusten/")
C.crawl("~/Music/Arne Nordheim - The Nordheim Tapes (2008) [FLAC]/CD 1/")
C.files
C.list
C.free
C.crawl
p = PathName("~/Music/Arne Nordheim - The Nordheim Tapes (2008) [FLAC]/CD 1/".escape("[]"))
p.isFolder
p.entries
p.deepFiles
p.deepFilesEscapedPath
p = PathName("~/Music/Arne Nordheim - Dodeka (2003) FLAC")
p.isFolder
p.deepFiles

	deepFiles {
		^this.entries.collect({ | item |
			if(item.isFile, {
				item
			},{
				item.deepFiles
			})
		}).flat
	}

C.escapeChars("/akjsdhasd/kj?asd/[]","[]?").postln
f.("/akjsdhasd/kjasd/","[]?")

f= {|path, chars| if(chars.size>0, {thisFunction.(path.escapeChar(chars[0]), chars[1..])}, {path})};
"/Users/salkin/Music/Arne Nordheim - Dodeka (2003) [FLAC]/".escapeGlobChars
String.escapeGlobChars
f.("/Users/salkin/Music/Arne Nordheim - Dodeka (2003) [FLAC]/", "[]")
f.("/Users/salkin/Music/Arne Nordheim - Dodeka (2003) [FLAC]/", "[] ()/")


PathName("/Users/salkin/Music/Arne Nordheim - The Nordheim Tapes (2008) [FLAC]/CD 1/").isFolder;
PathName("/Users/salkin/Music/Arne Nordheim - The Nordheim Tapes (2008) [FLAC]").deepFiles;

PathName("/Users/salkin/Music/Arne Nordheim - The Nordheim Tapes (2008) [FLAC]/").deepFiles
PathName("/Users/salkin/Music/Arne Nordheim - Dodeka (2003) FLAC/").isFolder
PathName("/Users/salkin/Music/Arne Nordheim - Dodeka (2003) FLAC/").deepFiles


PathName("/Users/salkin/Music/Arne Nordheim - The Nordheim Tapes (2008) [FLAC]/".replace("[", "\\[").replace("]", "\\]").replace("?","\\?")).isFolder;

("/Users/salkin/Music/Arne Nordheim - The Nordheim Tapes (2008) [FLAC]/".replace("[", "\\[").replace("]", "\\]")).class
PathName("/Users/salkin/Music/Arne Nordheim - The Nordheim Tapes (2008) \\[FLAC\\]/").isFolder


"/Dodeka (203) [FLAC]/".replace("[", "\\\\[")

"\\"++"\\"++"["
"\\\\["

~n = SoundFile.use("/Users/salkin/Sounds/tofuya.wav", {arg qfile; qfile.numChannels})
~n

"asd".scrable
