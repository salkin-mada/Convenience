{ | numchans |
	"loading reverb.scd".postln;

	(
		verb: { | in, time=5, damp=0.5, size=1.0, early=0.707, moddepth=0.1, modfreq=2.0, low=1.0, mid=1.0, high=1.0, lowcut=500.0, highcut=2000.0 |
			Array.fill(numchans, { | cNum |
				JPverb.ar(
					in[cNum],
					t60: time,
					damp: damp,
					size: size,
					earlyDiff: early,
					modDepth: moddepth,
					modFreq: modfreq,
					low: low,
					mid: mid,
					high: high,
					lowcut: lowcut,
					highcut: highcut
				)
			})
		},
		hall: { | in, time=5, stereo=0.5, lofreq=200, loratio=0.5, hifreq=4000, hiratio=0.5, early=0.5, late=0.5, modrate=0.2, moddepth=0.3 |
			Array.fill(numchans, { | cNum |
				NHHall.ar(
					Splay.ar(in[cNum]), // ensure it is stereo in
					rt60: time,
					stereo: stereo,
					lowFreq: lofreq,
					lowRatio: loratio,
					hiFreq: hifreq,
					hiRatio: hiratio,
					earlyDiffusion: early,
					lateDiffusion: late,
					modRate: modrate,
					modDepth: moddepth
				)
			})
		}
	)
}
