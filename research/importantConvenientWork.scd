
(
[
	"/home/salkin/soundfiles lenovo/ZINK/UX0090_ 001 - 001_001.WAV",
	"/home/salkin/soundfiles lenovo/ZINK/UX0091_ 001 - 001_002.WAV",
	"/home/salkin/soundfiles lenovo/ZINK/UX0092_ 001 - 001_003.WAV",
	"/home/salkin/Desktop/homperc/Convenience Folder/chowning.wav",
].do{arg path;

	SoundFile.use(path, {arg file; file.numChannels}).postln

}
)



Ndef(\asdf).play


Convenience.crawl("A:/LYDFILER/Drums/CR-909/samples/CR-909 FULL", 1)
Convenience.list
Convenience.p(\ad, folder: \midtom, dur: 3, tuningOnOff:1, degree: Pseq([1,5,9], inf), octave: 6, tempo: 5)
Convenience.p(\dr, folder: \hihat, dur: 2, tuningOnOff:1, degree: Pseq([1,5,9], inf), octave: 6, tempo: 10)




(
SynthDef(\ping, {|freq|
	var e, z;
	e= EnvGen.ar(Env.perc(0, 0.1), doneAction:2);
	z= SinOsc.ar(freq.dup, 0, 0.2);
	OffsetOut.ar(0, z*e);
}).add;
)

Pdef(\fir, Pbind(\instrument, \ping, \dur, 2, \degree, Pseq([1,5,9], inf), \tempo, 4)).play
Pdef(\ota, Pbind(\instrument, \ping, \dur, 3, \degree, Pseq([1,5,9], inf), \octave, 6)).play

//-- go from 1.5 to 0.8 and end on downbeat 4 sec from now
~from= 0.8;
~to= 4.8;
~sec= 12;
c= TempoClock(~from);
c.sched(c.timeToNextBeat(1), {Synth(\ping, [\freq, 800]); 1});
(
SystemClock.sched(~sec, {Synth(\ping, [\freq, 1200]); ~to.reciprocal});
c.sync(~to, ~sec);
)

//-- dec temo.  after 8.1 sec tempo 1.1
~from= 1.9;
~to= 1.1;
~sec= 8.1;
c= TempoClock(~from);
c.sched(c.timeToNextBeat(1), {Synth(\ping, [\freq, 800]); 1});
(
SystemClock.sched(~sec, {Synth(\ping, [\freq, 1200]); ~to.reciprocal});
c.sync(~to, ~sec);
)

//-- inc
~from= 1.1;
~to= 1.9;
~sec= 8.1;
c= TempoClock(~from);
c.sched(c.timeToNextBeat(1), {Synth(\ping, [\freq, 800]); 1});
(
SystemClock.sched(~sec, {Synth(\ping, [\freq, 1200]); ~to.reciprocal});
c.sync(~to, ~sec);
)

//-- quick adjust.  interpolation suffers a little.
~from= 1.0;
~to= 1.2;
~sec= 3.3;
c= TempoClock(~from);
c.sched(c.timeToNextBeat(1), {Synth(\ping, [\freq, 800]); 1});
(
SystemClock.sched(~sec, {Synth(\ping, [\freq, 1200]); ~to.reciprocal});
c.sync(~to, ~sec);
)


//-- 2 tempoclocks!!!  syncs them after 4.7 seconds
~from1= 1.02;
~to1= 1.8;
~from2= 1.3;
~to2= 1.8;
~sec= 4.7;
c= TempoClock(~from1);
d= TempoClock(~from2);
c.sched(c.timeToNextBeat(1), {Synth(\ping, [\freq, 800]); 1});
d.sched(d.timeToNextBeat(1), {Synth(\ping, [\freq, 1200]); 1});
(
c.sync(~to1, ~sec);
d.sync(~to2, ~sec);
)

//-- 2 tempoclocks synced almost at once
~from1= 1.02;
~to1= 1.8;
~from2= 1.3;
~to2= 1.8;
~sec= 10;
c= TempoClock(~from1);
d= TempoClock(~from2);
c.sched(c.timeToNextBeat(1), {Synth(\ping, [\freq, 800]); 1});
d.sched(d.timeToNextBeat(1), {Synth(\ping, [\freq, 1200]); 1});
(
c.sync(~to1, ~sec);
d.sync(~to2, ~sec);
)


//-- resolution make the interpolation smoother.  this will update tempo 5times/beat (0.2)

(
~from= 0.1;
~to= 1.5;
~sec= 3.3;
c= TempoClock(~from);
a= Pbind(\degree, Pseq([0, 5, 3, 2], inf), \dur, 0.125, \amp, 0.1).play(c);
c.sync(~to, ~sec, 0.1);
)














d = (a: "hello", b: "robot", c: [1, 2, 3]);
(
d.keysValuesChange { |key, value|
	switch(key)
	{\a} {123}
	{\b} {"tennis"}
	{\c} {[[1,2],[6,5,4]]}
};
)
d;

c = Sleet.new(2)
c.get(\chorus)

r = Array.with("chorus","freeverb","smear");
c.get(r[0].asSymbol)

Convenience.properties.asDict.includesKey(\dur)
Dictionary














Convenience.crawl("/media/salkin/MOVE SSD/LYDFILER/Drums/Sound Master, Memory Rhythm SR-88")
Convenience.list
Convenience.p(\asd, dur: 0.9, rate: 0.8, loop: 1, amp: 0.9, out: 0)
Convenience.p(\asd, dur: 0.1, rate: 0.8, loop: 0, index: Pwhite(0,30).round, amp: 0.4, out: 0)
Convenience.s(\asd)
Convenience.gpp(\asd, \dur).source.asStream.nextN(1)
Convenience.properties
Convenience.fxs.choose
//Convenience.pfx(\asd, [Convenience.fxs.choose, Convenience.fxs.choose], nil , \nam, \yo).fadeTime_(4)
Convenience.pfx(\asd, [Convenience.fxs.choose, Convenience.fxs.choose]).fadeTime_(40)


(
Pdefn((\asd++"_folder").asSymbol, 'Sound_Master_Memory_Rhythm_SR_88',
Pdefn((\asd++"_dur").asSymbol, Pwhite(0.25,1.3));
Pdefn((\asd++"_tuningOnOff").asSymbol, 0);
Pdefn((\asd++"_degree").asSymbol, 0);
Pdefn((\asd++"_octave").asSymbol, 3);
Pdefn((\asd++"_index").asSymbol, Pseq([0,3,5,2], inf));
Pdefn((\asd++"_rate").asSymbol, 0.8);
Pdefn((\asd++"_loop").asSymbol, 1);
Pdefn((\asd++"_sustain").asSymbol, 1);
Pdefn((\asd++"_release").asSymbol, 0.8);
Pdefn((\asd++"_stretch").asSymbol, Pwhite(0.1,3.5));
Pdefn((\asd++"_pan").asSymbol, Pwhite(-1.0));
Pdefn((\asd++"_amp").asSymbol, Pwhite(0.8,1.0)*0.8);
Pdefn((\asd++"_pos").asSymbol, 0);
Pdefn((\asd++"_attack").asSymbol, 0.01);
Pdefn((\asd++"_cutoff").asSymbol, Pwhite(4000,8000));
Pdefn((\asd++"_out").asSymbol, 0))
)


(
Pdefn((\asd++"_folder").asSymbol, 'Sound_Master_Memory_Rhythm_SR_88');
Pdefn((\asd++"_dur").asSymbol, Pseq([1/8,1/8,1/8,1/8,1/8,2/8,3/8,4/8], inf));
Pdefn((\asd++"_tuningOnOff").asSymbol, 0);
Pdefn((\asd++"_degree").asSymbol, 0);
Pdefn((\asd++"_octave").asSymbol, 3);
Pdefn((\asd++"_index").asSymbol, Pseq([0,3,5,2], inf));
Pdefn((\asd++"_rate").asSymbol, 1.8);
Pdefn((\asd++"_loop").asSymbol, 0);
Pdefn((\asd++"_sustain").asSymbol, 1);
Pdefn((\asd++"_release").asSymbol, 0.8);
Pdefn((\asd++"_stretch").asSymbol, Pwhite(1.0,1.1));
Pdefn((\asd++"_pan").asSymbol, Pwhite(-1.0));
Pdefn((\asd++"_amp").asSymbol, Pwhite(0.8,1.0)*1.4);
Pdefn((\asd++"_pos").asSymbol, 0);
Pdefn((\asd++"_attack").asSymbol, 0.01);
Pdefn((\asd++"_cutoff").asSymbol, Pwhite(4000,8000));
Pdefn((\asd++"_out").asSymbol, 0)
)

Pdefn((\asd++"_cutoff").asSymbol).source.asStr.nextN(2)



Pdef(\asd).stop

Ndef(\asd).source = Pdef(\asd);
Ndef(\asd).play;
Ndef(\fx)[2].source.value.value

z = Sleet.new(numChannels: 2);
z.modules.keys.do{ | module| module.do{arg mod; mod.postln}}
Ndef.all[Server.default.asSymbol].do{arg i; i.postln}

Server.default
Ndef(\asd).play(,)
(
Ndef(\asd)[1] = \filter -> {|in, pitch=1, disp=0.0|
	PitchShift.ar(in, 0.25, pitch, disp,)
};
)
[0,1].do{arg i; i.postln}
(
Ndef(\asd)[2] = \filter -> {|in, revtime=3, damping=0.5|
	GVerb.ar(in,10,revtime,damping)
};
)
z = Sleet.new(numChannels: 2);
(
Ndef(\asd)[3] = \filter -> z.modules[\spectral].choose
)
arg
(
Ndef(\asd)[4] = \filter -> z.modules[\granular].choose
)

(
Ndef(\asd)[5] = \set -> Pbind(
	\dur, Pdefn((\asd++"_dur").asSymbol),
	\wet1, Psinen(0.2)*0.6,
	\pitch, Pdefn((\asd++"_index").asSymbol),
	\disp, Pdefn((\asd++"_index").asSymbol)/10,
	\wet2, Psinen(0.201)*0.3,
	\revtime,4,
	\damping, Psinen(0.3).linlin(0.0,1.0,0.7,1.0),
	\wet3, Psinen(0.1),
	\wet4, Psinen(0.22)*0.9,
);
)



Ndef(\fx).stop;



















(
~listen = [
	'salat' -> \totales,
	'mayo' -> 123,
	'skinke' -> 0.65,
	'ost' -> \nil,
];
)
~tingene = Dictionary.with(*~listen.collect{arg i; i})
~tingene.asArgsString(delim: ",", equalSign: "=", prepend: "| ", append: " |")
r



r = "arg ter =123, htre = 43)"





~properties.collect{arg i; i}.asArgsString(delim: ",", equalSign: "=", prepend: "| ", append: " |")

~properties.keys
~properties.values
~properties = Dictionary.with(*~propertyList.collect{arg i; i})
(
~propertyList = [
	'name' -> \test,
	'type' -> \Convenience,
	'out' -> 0,
	'folder' -> \nil,
	'index' -> 1,
	'dur' -> 8,
	'stretch' -> 1.0,
	'pos' -> 0,
	'loop' -> 0,
	'rate' -> 1,
	'degree' -> 0,
	'octave' -> 3,
	'root' -> 0,
	'scale' -> \nil,
	'cutoff' -> 22e3,
	'bass' -> 0,
	'pan' -> 0,
	'spread' -> 0,
	'amp' -> 0.5,
	'attack' -> 0.1,
	'sustain' -> 1.0,
	'release' -> 0.5,
	'tempo' -> \nil,
	'tuningOnOff' -> 0,
	'basefreq' -> 440,
	'fftOnOff' -> 0,
	'binRange' -> 20,
	'pitchShiftOnOff' -> 0,
	'pitchRatio' -> 1.0,
	'formantRatio' -> 1.0
];
)

~properties.collect{arg i; i}
~properties.reject({arg item; item == \name})

// do, collect, reject, select
d = Dictionary[\a -> "hello", \b -> "robot", \c -> [1, 2, 3]];
d = (a: "hello", b: "robot", c: [1, 2, 3]); // equivalent
d.keys.reject { |item| item == \name}.postln;
d.select { |item| item.size > 4 };



r = d.keysValuesDo

r












(

Event.addEventType(\Convenience, {
	var numChannels, scaling, fft, pitchshift;

	if (~buffer.isNil) {
		var folder = ~folder;
		var index = ~index;
		//if (folder.isNil.not) {
		//var index = ~index ? 0;

		~buffer = Convenience.get(folder, index)
		//} {

		// pair split if folder is nil
		// var sample = ~sample;
		// if (sample.isNil.not) {
		// 	var pair, folder, index;
		// 	pair = sample.split($:);
		// 	folder = pair[0].asSymbol;
		// 	index = if (pair.size == 2) { pair[1].asInt } { 0 };
		// 	~buffer = Convenience.get(folder, index)
		// }
		//}
	};

	numChannels = ~buffer.bufnum.numChannels;

	scaling = ~tuningOnOff;
	if(scaling.isNil) {scaling = 0};
	fft = ~fftOnOff;
	if(fft.isNil) {fft = 0};
	pitchshift = ~pitchShiftOnOff;
	if(pitchshift.isNil) {pitchshift = 0};

	case
	// favoring pitchshift
	{fft == 1 and: pitchshift == 0 and: scaling == 0} {
		~instrument = \ConvenienceBufBins;
		//"FFT".postln;
	}
	{fft == 1 and: pitchshift == 0 and: scaling == 1} {
		~instrument = \ConvenienceBufBinsScale;
		//"FFT+SCALING".postln;
	}
	{pitchshift == 1 and: scaling == 0} {
		~instrument = \ConveniencePitchShift;
		//"PITCHSHIFT".postln;
	}
	{pitchshift == 1 and: scaling == 1} {
		~instrument = \ConveniencePitchShiftScale;
		//"PITCHSHIFT+SCALING".postln;
	}
	{scaling == 1 and: fft == 0 and: pitchshift == 0} {
		//"SCALING -- ".post;
		switch(numChannels,
			1, {
				~instrument = \ConvenienceMonoScale;
				//"mono".postln;
			},
			2, {
				~instrument = \ConvenienceStereoScale;
				//"stereo".postln;
			},
			{
				~instrument = \ConvenienceMonoScale;
				//"mono-default".postln;
			}
		);

	}
	{
		//"NORMALES -- ".post;
		switch(numChannels,
			1, {
				~instrument = \ConvenienceMono;
				//"mono".postln;
			},
			2, {
				~instrument = \ConvenienceStereo;
				//"stereo".postln;
			},
			{
				~instrument = \ConvenienceMono;
				//"mono-default".postln;
			}
		);
	};

	~type = \note;
	~bufnum = ~buffer.bufnum;
	currentEnvironment.play
});

~name = \the;

~propertyList = [
	'name' -> ~name,
	'type' -> \Convenience,
	'out' -> 0,
	'folder' -> \nil,
	'index' -> 1,
	'dur' -> 4,
	'stretch' -> 1.0,
	'pos' -> 0,
	'loop' -> 0,
	'rate' -> 1,
	'degree' -> 0,
	'octave' -> 4,
	'root' -> 0,
	'scale' -> \nil,
	'cutoff' -> 22e3,
	'bass' -> 0,
	'pan' -> 0,
	'spread' -> 0,
	'amp' -> 0.5,
	'attack' -> 0.01,
	'sustain' -> 1.0,
	'release' -> 0.5,
	'tempo' -> \nil,
	'tuningOnOff' -> 1,
	'basefreq' -> 440,
	'fftOnOff' -> 0,
	'binRange' -> 20,
	'pitchShiftOnOff' -> 0,
	'pitchRatio' -> 1.0,
	'formantRatio' -> 1.0
];


//keyValuePairsFromArgs()
{ arg name = ~name, type=\Convenience, folder, scale, tempo;

	var properties, pdefnProperties = List.new;

	properties = Dictionary.with(*~propertyList.collect{arg i; i});

	properties.keysDo{ | key |
		if (((key == \name) or: (key == \tempo)).not, {
			pdefnProperties.add(key)
		})
	};

	if (folder.isNil, {
		if(Convenience.folders.asArray[0].isNil.not, {
			folder = Convenience.folders.asArray[0];
		}, {Error("Convenience:: no buffers available").throw; ^nil})
	});

	if(scale.isNil, {
		scale = Scale.chromatic;
	});

	if ( Main.packages.asDict.includesKey(\Utopia) == true, {
		if (tempo.class == BeaconClock, {
			// great do nothing
			//"\ttempo is BeaconClock controlled".postln
		}, {
			tempo = TempoClock(tempo);
			//"\tusing tempoclock".postln;
		})
	}, {
		tempo = TempoClock(tempo);
	});

	//pdefnProperties = properties.keysValuesDo{ | key, value |
	pdefnProperties = pdefnProperties.collect{ | key |
		var pdefn;
		switch(key)
		{\type} {[key.asSymbol, type]}
		{\scale} {[key.asSymbol, scale]}
		{
			//pdefn = Pdefn((name.asString++"_"++key.asString).asSymbol, 1);
			pdefn = Pdefn((name.asString++"_"++key.asString).asSymbol, properties.at(key));
			[key.asSymbol, pdefn]
		}
	};

	//pdefnProperties.do{arg i; i.postln};

	Pdef(name,
		/*Pbind().patternpairs_(pdefnProperties.keysValuesDo{ | key, value |
		[key, value]})*/
		Pbind().patternpairs_(pdefnProperties.collect{ | property, pdefn |
			property;
		}.flat)
	).play(tempo);

	/*Pdefn((name.asString++"_folder").asSymbol, Convenience.folderNum(1));
	Pdefn((name.asString++"_dur").asSymbol, Pwhite(0.25,1.3));
	Pdefn((name.asString++"_tuningOnOff").asSymbol, 0);
	Pdefn((name.asString++"_degree").asSymbol, 0);
	Pdefn((name.asString++"_octave").asSymbol, 3);
	Pdefn((name.asString++"_index").asSymbol, Pseq([0,3,5,2], inf));
	Pdefn((name.asString++"_rate").asSymbol, 0.8);
	Pdefn((name.asString++"_loop").asSymbol, 1);
	Pdefn((name.asString++"_sustain").asSymbol, 1);
	Pdefn((name.asString++"_release").asSymbol, 0.8);
	Pdefn((name.asString++"_stretch").asSymbol, Pwhite(0.1,3.5));
	Pdefn((name.asString++"_pan").asSymbol, Pwhite(-1.0));
	Pdefn((name.asString++"_amp").asSymbol, Pwhite(0.7,0.9));
	Pdefn((name.asString++"_pos").asSymbol, 0);
	Pdefn((name.asString++"_attack").asSymbol, 0.01);
	Pdefn((name.asString++"_cutoff").asSymbol, Pwhite(4000,8000));
	Pdefn((name.asString++"_out").asSymbol, 0)*/
}.value)


Pdef.all.do{arg pdef; pdef.stop; pdef.free; pdef.clear}
Pdefn.all.do{arg pdef; pdef.stop; pdef.free; pdef.clear}

Server.killAll

Convenience.crawl("A:/LYDFILER/Drums/Sound Master, Memory Rhythm SR-88")
Convenience.list
Convenience.p(\asd)

(
Pdefn((\asd++"_folder").asSymbol, Convenience.folderNum(1));
Pdefn((\asd++"_dur").asSymbol, Pwhite(0.25,1.3));
Pdefn((\asd++"_tuningOnOff").asSymbol, 0);
Pdefn((\asd++"_degree").asSymbol, 0);
Pdefn((\asd++"_octave").asSymbol, 3);
Pdefn((\asd++"_index").asSymbol, Pseq([0,3,5,2], inf));
Pdefn((\asd++"_rate").asSymbol, 0.8);
Pdefn((\asd++"_loop").asSymbol, 1);
Pdefn((\asd++"_sustain").asSymbol, 1);
Pdefn((\asd++"_release").asSymbol, 0.8);
Pdefn((\asd++"_stretch").asSymbol, Pwhite(0.1,3.5));
Pdefn((\asd++"_pan").asSymbol, Pwhite(-1.0));
Pdefn((\asd++"_amp").asSymbol, Pwhite(0.8,1.0)*1.4);
Pdefn((\asd++"_pos").asSymbol, 0);
Pdefn((\asd++"_attack").asSymbol, 0.01);
Pdefn((\asd++"_cutoff").asSymbol, Pwhite(4000,8000));
Pdefn((\asd++"_out").asSymbol, 0)
)

Convenience.properties
Pdef(\asd).stop


Ndef(\fx).source = Pdef(\asd);
(
Ndef(\fx)[1] = \filter -> {|in, pitch=1, disp=0.0|
	PitchShift.ar(in, 0.25, pitch, disp,)
};
)

(
Ndef(\fx)[2] = \filter -> {|in, revtime=3, damping=0.5|
	GVerb.ar(in,10,revtime,damping)
};
)

(
Ndef(\fx)[666] = \set -> Pbind(
	\dur, Pdefn((\asd++"_dur").asSymbol),
	\wet1, Psinen(0.2)*0.6,
	\wet2, Psinen(0.2)*0.4,
	\pitch, Pdefn((\asd++"_index").asSymbol),
	\disp, Pdefn((\asd++"_index").asSymbol)/10
);
)
Ndef(\fx).play;




(
)

~pdefn_properties.flat.do{arg item, i; item.postln; i.postln}

Pdefn('some_stretch').asStream.nextN(20)
Pdefn('some_dur').asStream.nextN(20)
x = Pseq([Pdefn(\some_dur)], inf).asStream;
t = Task({ loop({ x.next.postln; 0.3.wait }) }).play;


(
Pdef(name,
	Pbind(

	);
).play(tempo);
)




r =["aaq","name", nil].reject({arg item, i; item.isNil})


.performDegreeToKey


Convenience.crawl("A:/LYDFILER/Drums/Sound Master, Memory Rhythm SR-88")
Convenience.list

Convenience.p(\yoyoyoy)
.asArgsString()

Pdef(\halla).source.asStream.nextN(20)