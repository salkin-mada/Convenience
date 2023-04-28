(
// AbstractPlayControl-par
//
// create parallel streams of the provided function filling the NodeProxy channels.
// requires the functions output channels to be a multiple of the NodeProxy's numChannels.
AbstractPlayControl.proxyControlClasses.put(\par, SynthDefControl);
AbstractPlayControl.buildMethods.put(\par, #{ | func, proxy, channelOffset = 0, index |
	var funcNumChannels;

	func.isKindOf(ArrayedCollection).if({
		funcNumChannels = func[1];
		func = func.first;
	}, {
		try({
			funcNumChannels = (func.value.shape ?? {[1]}).first;
		},{|exeption|
			(exeption.selector == \addKr).if({
				// assume mono
				"NodeProxy: NamedControl in function, please provide numChannels explicitely:".postln;
				"\t \par -> [{...}, <numChannels>]".postln;
				funcNumChannels = 1;
			}, {
				exeption.throw
			})
		})
	});

	if ((proxy.numChannels % funcNumChannels) != 0) {
		Error("NodeProxy input (par): number of proxy channels need to be multiple of function output channels").throw;
	};

	{ | out |
		var env, ctl = NamedControl.kr(("mix" ++ (index ? 0)).asSymbol, 1.0);
		var funcControls, outSnd;

		// create named controls '<argname>s' whilst retaining order of function args
		funcControls = func.def.keyValuePairsFromArgs.clump(2).collect{|args, i|
			var key, val;
			#key, val = args;

			// \idx arg is used to hand index into function
			if (key == \idx, {
				Array.series((proxy.numChannels/funcNumChannels).asInteger)
			}, {
				NamedControl.kr(
					name: (key ++ $s  ++ (index ? 0)),
					values: (val ? 0).dup(proxy.numChannels/funcNumChannels)
				)
			})

			//
			// NamedControl.kr(
			// 	(args[0] ++ $s ++ (index ? 0)),
			// 	(args[1] ? 0).dup(proxy.numChannels/funcNumChannels)
			// )
		};

		// embed function synthesis
		if (funcControls.isEmpty, {
			// no args given
			outSnd = (proxy.numChannels/funcNumChannels).asInteger.collect{
				SynthDef.wrap(func, nil);
			}
		}, {
			// args given
			outSnd = funcControls.flop.collect{|args, i|
				// args.postln;
				SynthDef.wrap(func, nil, args);
			}.flatten;
		});


		if(proxy.rate === 'audio') {
			env = ctl * EnvGate(i_level: 0, doneAction: 2, curve: \sin);
			Out.ar(out, env * outSnd)
		} {
			env = ctl * EnvGate(i_level: 0, doneAction: 2, curve: \lin);
			Out.kr(out, env * outSnd)
		};
	}.buildForProxy( proxy, channelOffset, index )
});

// specs

(0..1000).do{|i|
	Spec.add(
		("wet"++i).asSymbol, [0, 1].asSpec
	);
	Spec.add(
		("mix"++i).asSymbol, [0, 1].asSpec
	);
}
)


////////////// example usage

// make n 8-channel Ndef
Ndef(\a).ar(8)


// show edit window
Ndef(\a).edit(30)


// play on two channels (this wraps the 8 channels onto 2)
Ndef(\a).play(0, numChannels: 2, vol: 0.2)

// create an 8-channel sound by instantiating 8 times the below monosound
Ndef(\a)[0] = \par -> {|freq = 444, amp = 0.1, lpFreq = 1200, lpRq = 0.1| RLPF.ar(Blip.ar(freq.lag(4), 5), lpFreq.lag(4), lpRq.lag(4)) * amp };

// set some arbitrary parameters of the sound
Ndef(\a).setn(\freqs0, {exprand(100, 400)}!8);
Ndef(\a).setn(\lpFreqs0, {exprand(1000, 4000)}!8);
Ndef(\a).setn(\lpRqs0, {exprand(1, 0.1)}!8);


// add a variation of the first sound in 2nd slot
Ndef(\a)[1] = \par -> {|freq = 444, amp = 0.1, lpFreq = 1200, lpRq = 0.1| RLPF.ar(Blip.ar(freq.lag(4), 5), lpFreq.lag(4), lpRq.lag(4)) * amp };

// set its mix param (amplitude)
Ndef(\a).set(\mix1, 0.3);

// set some arbitrary parameters of the 2nd sound
Ndef(\a).setn(\freqs1, {exprand(1000, 4000)}!8);
Ndef(\a).setn(\lpFreqs1, {exprand(1000, 4000)}!8);
Ndef(\a).setn(\lpRqs1, {exprand(1, 0.1)}!8);


// filter sound globally
Ndef(\a)[10] = \filter -> {|in, lpFreq = 1000, lpRq = 0.1| RLPF.ar(in, lpFreq, lpRq)}

Ndef(\a).release
Ndef(\a).clear
