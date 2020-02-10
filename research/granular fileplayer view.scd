(

// SynthDef example from Buffer Granulation tutorial

SynthDef(\gran_1a, { arg out = 0, bufNum = 0, posLo = 0.0, posHi = 1.0,
posRate = 1, posDev = 0.01, trigRate = 100, granDur = 0.1, rate = 1.0,
    panMax = 1, amp = 0.1, interp = 4;

    var trig, pan, pos, bufDur, bufDurSection, posDif;

    posDif = posHi - posLo;
    bufDur = BufDur.kr(bufNum);
    bufDurSection = bufDur * posDif;
    trig = Impulse.kr(trigRate);
    pos = posLo * bufDur +
(Phasor.ar(0, BufRateScale.kr(bufNum) * posRate / SampleRate.ir, posLo * bufDur, posHi * bufDur) + (TRand.kr(-0.5 * posDev, 0.5 * posDev, trig) * bufDur)).mod(bufDurSection);
    pan = Demand.kr(trig, 0, Dseq([panMax, panMax.neg],inf) * 0.999);
Out.ar(out, TGrains.ar(2, trig, bufNum, rate, pos, granDur, pan, amp, interp));
    }, metadata: (
        specs: (
            posLo: [0.0, 1.0, \lin, 0.01, 0],
            posHi: [0.0, 1.0, \lin, 0.01, 0],
            posRate: [0.1, 2, \lin, 0.01, 1],
            posDev: [0, 0.2, 5, 0, 0.01],
            granDur: [0.01, 0.3, \lin, 0.01, 0.1],
            trigRate: [1, 200, \lin, 0.01, 100],
            rate: [0.1, 2, \lin, 0.01, 1],
            panMax: [0.0, 1, \lin, 0.005, 0.8],
            amp: [0.0, 0.5, \lin, 0.005, 0.25]
        )
    )
).add;


// Function to generate some SoundFileViews from one source

~soundFileViewN = { | n = 3, name = "sounds/a11wlk01.wav" |
	var v, w, f, a = 0!n;
	w = Window.new(name, Rect(200, 250, 740, n * 65 + 35)).front;
	w.view.decorator = FlowLayout(w.view.bounds, 20@20, 20@5);
	f = SoundFile.new;
	f.openRead(Platform.resourceDir +/+ name);
	n.do { |i|
		a[i] = SoundFileView(w.view, 700@60) ;
		a[i].soundfile = f;
		a[i].read(0, f.numFrames);
	};
	a
};

// Function to add hooks in both directions

~linkGuis = { |soundFileViews, varGui,
	posSliderIndices = #[1,2], synthSliderNum = 10|
	soundFileViews.do { |x,i|
		x.mouseUpAction = {
			var posData, posLo, posHi;
     			posData = x.selections[x.currentSelection] /
                	x.soundfile.numFrames;
        		posLo = posData[0];
        		posHi = posLo + posData[1];
        		varGui.updateSynthSliders(\posLo, i, posLo);
        		varGui.updateSynthSliders(\posHi, i, posHi);
		};
		posSliderIndices.do { |j|
			varGui.addSliderAction(
				{
					|slider|
					var lo, hi;
					lo = varGui.synthCtrSliders[10 * i +
						posSliderIndices[0]].();
					hi = varGui.synthCtrSliders[10 * i +
						posSliderIndices[1]].();
					soundFileViews[i].setSelectionStart(
						0,
						soundFileViews[0].numFrames * lo
					);
					soundFileViews[i].setSelectionSize(
						0,
						soundFileViews[0].numFrames * (hi - lo)
					);
				},
				\synth,
				10 * i + j
			)
		};
	}
};

b = Buffer.read(s, Platform.resourceDir +/+ "sounds/a11wlk01.wav");
)

// choose number and build
// positions are linked when set via guis

(
n = 4;
a = ~soundFileViewN.(n); // returns array of SoundFileViews
v = \gran_1a.sVarGui([\bufNum, b.bufnum], num: n).gui(tryColumnNum: 2);
~linkGuis.(a, v);
)