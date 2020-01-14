{ | numchans |
	"loading timbre.scd".postln;

	(
		freqshift: {|in, freq=0.01, phase=0|
			Array.fill(numchans, {|cNum|
				FreqShift.ar(in[cNum], freq * {rrand(0.95,1.0)},  phase: phase * {rrand(2pi,-2pi)})
			})
		},

		conv: { | in, freq = 220, delaytime = 0.5, decaytime = 1.0 |
	        Array.fill(numchans, {|cNum|
		        Convolution.ar(
					in[cNum], 
				    AllpassC.ar(
						in[cNum] * LFSaw.ar(freq), 
						1.0, 
						delaytime, 
						decaytime
					),
			        framesize: 1024,
					mul: 0.4
				)
	        })
        },

		ring: { |in, ringf = 0.5|
			var mod = SinOsc.ar(ringf);
			ring1(in, mod);
		},
		// from SuperDirt
		triode: {|in, triode=0.5|
			var sc = triode * 10 + 1e-3;
			var sig = in;

			(sig * (sig > 0)) + (tanh(sig * sc) / sc * (sig < 0));
		},
		// stolen from SuperDirt
		shaper: { |in, shape = 0|
			var signal = in;
			shape = shape.clip(0.0,0.90);

			shape = min(shape, 1.0 - 4e-10); // avoid division by zero
			//amp = 1.0 - (0.15 * shape / (shape + 2.0)) * amp; // optional gain comp
			shape = (2.0 * shape) / (1.0 - shape);

			LeakDC.ar((1 + shape) * signal / (1 + (shape * abs(signal))));
		},

		// Also taken from SuperDirt
		bitcrush: { |in, crush = 0.1|
			var signal = in;
			signal.round(0.5 ** (crush.linlin(0.0,1.0,16,2)-1));
		},
		waveloss: {|in, drop=0.5|
			WaveLoss.ar(in, drop * 40, 40, 2)
		}/*,
		lofi: {|in, lofirate=0.5, lofismooth=0.9|
			SmoothDecimator.ar(in, lofirate.linexp(0.0,1.0,44100,512),  smoothing: lofismooth,  mul: 1.0,  add: 0)
		}*/
	)
}


/* 
// Ring modulation stolen from SuperDirt. ring=modilation amount, ringf=modfreq,ringdf=slide in modfreq
			dirtring: { |in, ringf = 0.5, ringdf=0.15|
				var signal, mod;
				ringf = ringf.linlin(-1.0,1.0,0.0001,1.0);
				signal = in;
				mod = SinOsc.ar(XLine.kr(ringf, ringf + ringdf.linlin(-1.0,1.0,0.001,1.0)).linexp(0.0,1.0,20,20000));
				ring1(signal, mod)/2;
			},
			// Taken from Thor Magnussons book Scoring Sound: https://leanpub.com/ScoringSound/read#leanpub-auto-flanger 
			flanger: { |in, flangdelay=0.1, flangdepth=0.08, flangrate=0.06, flangfb=0.01|
				var input, maxdelay, maxrate, dsig, mixed, local;
				maxdelay = 0.013;
				maxrate = 10.0;
				input = in;
				flangdelay = flangdelay.linlin(-1.0,1.0,0.0001,0.1);
				flangrate = flangrate.linlin(-1.0,1.0,0.00001,1.0);
				flangdepth = flangdepth.linlin(-1.0,1.0,0.0,1.0);
				flangfb = flangfb.linlin(-1.0,1.0,0.0,1.0);

				local = LocalIn.ar(numchans, 0.0);

				dsig = AllpassC.ar( 
					input + (local * flangfb),
					maxdelay * 2,
					// very similar to SinOsc (try to replace it) - Even use LFTri
					LFPar.kr( 
						flangrate * maxrate,
						0,
						flangdepth * maxdelay,
						flangdelay * maxdelay
					),
					0
				);

				mixed = input + dsig;
				LocalOut.ar(mixed);
				mixed;
			}, 

			// Taken from Thor Magnussons book Scoring Sound: https://leanpub.com/ScoringSound/read#leanpub-auto-chorus 
			chorus: { |in, chpredelay=0.08, chrate=0.05, chdepth=0.1, chphasediff=0.5|
					var sig, modulators, numDelays = 12;
					chpredelay = chpredelay.linlin(-1.0,1.0,0.0001,0.25);
					chrate = chrate.linexp(-1.0,1.0,0.0001,10.25);
					chdepth = chdepth.linlin(-1.0,1.0,0.0,1.0);
					chphasediff = chphasediff.linlin(-1.0,1.0,0.00001,1.0);

					in = in * numDelays.reciprocal;
					modulators = Array.fill(numDelays, {arg i;
						LFPar.kr(chrate * rrand(0.94, 1.06), chphasediff * i, chdepth, chpredelay)}
					); 
					sig = DelayC.ar(in, 0.5, modulators);  
					numDelays.reciprocal * sig.sum!numchans;
				}, */