Salk { // the idea is to make a combination of RedGrain & PlayBufCf ++

	*ar { arg numChannels, bufnum=0, rate=1.0, trigger=1.0, startPos=0.0, loop = 0.0,
			lag = 0.1, n = 2;

		var index, method = \ar, on;

		switch ( trigger.rate,
			 \audio, {
				index = Stepper.ar( trigger, 0, 0, n-1 );
			 },
			 \control, {
				index = Stepper.kr( trigger, 0, 0, n-1 );
				method = \kr;
			},
			\demand, {
				trigger = TDuty.ar( trigger );
				index = Stepper.ar( trigger, 0, 0, n-1 );
			},
			{ ^PlayBuf.ar( numChannels, bufnum, rate, trigger, startPos, loop ); }
		);

		on = n.collect({ |i|
			InRange.perform( method, index, i-0.5, i+0.5 );
		});

		switch ( rate.rate,
			\demand,  {
				rate = on.collect({ |on, i|
					Demand.perform( method, on, 0, rate );
				});
			},
			\control, {
				rate = on.collect({ |on, i|
					Gate.kr( rate, on );
				});
			},
			\audio, {
				rate = on.collect({ |on, i|
					Gate.ar( rate, on );
				});
			},
			{
				rate = rate.asCollection;
			}
		);

		if( startPos.rate == \demand ) {
			startPos = Demand.perform( method, trigger, 0, startPos )
		};

		lag = 1/lag.asArray.wrapExtend(2);

		^Mix(
			on.collect({ |on, i|
				PlayBuf.ar( numChannels, bufnum, rate.wrapAt(i), on, startPos, loop )
					* Slew.perform( method, on, lag[0], lag[1] ).sqrt
			})
		);

	}



SalkGrain : Salk {

	var <>delta= 0.005, <>buf, <>rate= 1, <>pos= 0, <>dur= 0.2, <>pan= 0, <>amp= 1,

		<>mute= false, <>latency= 0.05, <server, task;

	*new {|server|

		^super.new.initSalk(server);

	}

	initSalk {|argServer|

		server= argServer ?? Server.default;

	}

	*initClass {

		ServerBoot.addToAll({			//build synthdef at server boot

			SynthDef(\redGrain, {

				|out= 0, bufnum= 0, rate= 1, pos= 0, dur= 1, pan= 0, amp= 1|

				var e, z;

				e= EnvGen.ar(Env.sine(dur), 1, amp*0.1, doneAction: 2);

				z= PlayBuf.ar(

					1,

					bufnum,

					rate*BufRateScale.ir(bufnum),

					1,

					pos*BufSamples.ir(bufnum),

					1

				);

				OffsetOut.ar(out, Pan2.ar(z*e, pan));

			}).add;

		});

	}

	start {|out= 0|

		var synthName= this.prSynthName;

		if(buf.isNil, {"Salk: set a buffer first".warn; this.halt});

		mute= false;

		task= Task({

			inf.do{|i|

				if(mute.not, {

					server.sendBundle(latency, [\s_new, synthName, -1, 0, 0,

						\out, out,

						\bufnum, buf.value(i).bufnum,

						\rate, rate.value(i),

						\pos, pos.value(i),

						\dur, dur.value(i),

						\pan, pan.value(i),

						\amp, amp.value(i)

					]);

				});

				delta.value(i).wait;

			};

		}).play;

	}

	stop {task.stop}

	pause {task.pause}

	resume {task.resume}

	prSynthName {^"Salk"}

}



//another version

SalkFR : Salk {

	*initClass {

		ServerBoot.addToAll({		//build synthdef at server boot

			SynthDef(\SalkFR, {

				|out= 0, bufnum= 0, rate= 1, pos= 0, dur= 1, pan= 0, amp= 1|

				var e, z;

				e= EnvGen.ar(Env.sine(dur), 1, amp*0.1, doneAction: 2);

				z= PlayBuf.ar(

					1,

					bufnum,

					rate*BufRateScale.ir(bufnum),

					1,

					pos*BufSamples.ir(bufnum),

					1

				);

				Out.ar(out, Pan2.ar(z*e, pan));

			}).add;

		});

	}

	prSynthName {^"SalkFR"}

}
