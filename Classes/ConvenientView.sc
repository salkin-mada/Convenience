ConvenientView {

	*crawlerWindow{ |depth, server|
		var initpath;
		var cond = Condition(false);
		var win, sink, depthSetter;
		var winW = 280, winH = 340;
		var fontName = "Monospace", fontSize = 16, headerFontSize = 20;
		var crawlerWindowStayOpen = false;
		var recursiveGuard = true; // protect your mem and swap from insane typ 3e4 depth crawling
		var maxDepth = 99; // guard threshold

		var headerbackgroundColor = Color.fromHexString("#B1B137").alpha_(0.2);
		var headerStringColor = Color.fromHexString("#27236A");

		var sinkColor = Color.fromHexString("#B1B137").alpha_(0.1);
		var sinkStringColor = Color.fromHexString("#27236A");

		var sinkShapeColor = Color.fromHexString("#ED1256");

		// words
		var defaultSinkString = "drop folder here";

		// placements in win
		var buttonW = 190;
		var buttonH = 25;
		var buttonHorizPlacement = 37;

		var sinkHorizPlacement = 65;
		var sinkTextHorizPlacement = 70;
		var sinkTextW = 270;
		var sinkTextH = 30;


		// main
		win = Window.new("crawler"/*, resizable: false*/)
		.background_(Color.white)
		.setInnerExtent(winW, winH)
		.alwaysOnTop_(true)
		.front;

		// header text
		StaticText(win, Rect(0, 0, 280, 35)).align_(\center)
		.stringColor_(headerStringColor)
		.background_(headerbackgroundColor)
		.string_("choose init path")
		.font_(Font(fontName, headerFontSize, bold: true));

		// state
		Button(win, Rect(winW-buttonW/2, buttonHorizPlacement,buttonW, buttonH))
		.states_([
			["closing when done", Color.white, Color.blue],
			["staying open", Color.black, Color.magenta]
		])
		.font_(Font(fontName, fontSize))
		.action_({ | state |
			crawlerWindowStayOpen = state.value.asBoolean;
		});

		// SINK
		// receive path
		sink = DragSink(win, Rect(0, sinkHorizPlacement, winW, winW)).align_(\center);
		sink.string = defaultSinkString;
		sink.stringColor_(sinkStringColor);
		sink.background_(sinkColor)
		.font_(Font(fontName, fontSize));

		//sink text
		StaticText(win,Rect(40, sinkTextHorizPlacement, sinkTextW, sinkTextH))
		.string_("zZzzzZ")
		.font_(Font(fontName, fontSize));

		//this.sinkShapeHome(win);
		win.drawFunc = {
			Pen.translate(60, 100);
			1.do{
				// set the Color
				Pen.color = sinkShapeColor;
				Pen.addArc(100@100, 100, 45, 2.3);
				Pen.perform(\fill);
			}
		};
		win.refresh;

		// DEPTHSETTER
		depthSetter = TextField(win, Rect(235, buttonHorizPlacement, 30, 25)).align_(\center);
		depthSetter.string_(depth);
		//depthSetter.background_(Color.blue(alpha:0.5));

		depthSetter.action_{ | str |
			var integerGuard = true;
			str.value.do{ | char | if (char.digit > 9,{integerGuard = false})};
			if (recursiveGuard.not, {
				"recursiveGuard.not".postln;
			});
			if (integerGuard, {
				depth = str.value.asInteger;
				{ // Routine GREEN
					2.do{
						256.do{ | i |
							var radius = i.linlin(0,255,0.0,25.0);

							depthSetter.background_(Color.new255(0,i,0,i.linlin(0,255,255,0)));
							sink.background_(Color.new255(0,i.linlin(0,255,255,100),0,i));

							win.drawFunc = {
								Pen.translate((i/5).ceil, (i/2*pi/4).ceil);
								1.do{
									// set the Color
									Pen.color = Color.fromHexString("#ED1256");
									Pen.addArc(100@100, 100, i.linlin(0,255,0.45pi,0.65pi), 2.3);
									Pen.perform(\fill);
								}
							};

							win.refresh;
							0.001.wait;
						};
					};
					// back to normal
					sink.background_(sinkColor);
					//this.sinkShapeHome
					win.drawFunc = {
						Pen.translate(60, 100);
						1.do{
							// set the Color
							Pen.color = Color.fromHexString("#ED1256");
							Pen.addArc(100@100, 100, 45, 2.3);
							Pen.perform(\fill);
						}
					};
					win.refresh;
				}.fork(AppClock)
			}, {
				"please set depth with an integer".postln;
				{ // Routine RED
					2.do{
						var col = 0;
						256.do{
							depthSetter.background_(Color.new255(col,0,0,col.linlin(0,255,255,0)));
							sink.background_(Color.new255(col.linlin(0,255,255,0),0,0,col));
							col = col + 1;
							0.001.wait;
						};
					};
					// back to normal
					sink.background_(sinkColor);
				}.fork(AppClock)
			});
			//"loading depth is: %".format(integer.value).postln;
		};


		sink.receiveDragHandler = {
			sink.object = View.currentDrag.value;
			initpath = sink.object.value;
			//"initpath set from crawl gui: %".format(initpath).postln;
			// gui feedback for humans begin
			sink.string = "good choice!";
			sink.background_(Color.green);
			{// Routine for Condition trigger and feedback
				0.4.wait;
				// do real work behind sillyness
				cond.test = true;
				cond.signal;
				sink.background_(Color.fromHexString("#93CC41"));
				sink.stringColor_(Color.fromHexString("#5E0B5E"));
				sink.string = "crawling around";
				5.do{
					5.do{
						0.02.wait;
						sink.string = sink.string+".";
					};
					sink.string = "crawling around";
					0.05.wait;
				};
				sink.stringColor_(Color.fromHexString("#FD7119"));
				sink.string = "done crawling";
				//"\nother routine autoClose bool is %\n".format(crawlerWindowStayOpen).postln;
				if (crawlerWindowStayOpen == true, {
					1.25.wait;
					// reset, ready for more
					sink.string = defaultSinkString;
					sink.stringColor_(sinkStringColor);
					sink.background_(sinkColor);
				}, {
					0.4.wait;
					win.close
				});
			}.fork(AppClock);
			// gui feeback for humans end
			{ // Routine for the wait Condition
				cond.wait; // wait for dialog and input
				"\ncrawl:::going to parser".postln;
				// go to parser
				Convenience.prParseFolders(initpath, depth, server);
				"\ncrawl:::done parsing".postln;
			}.fork(AppClock)
		}
	}

	*sinkShapeHome { | win |
		// sink shape
		win.class.postln;
		/*win.drawFunc = {
		Pen.translate(60, 100);
		1.do{
		// set the Color
		Pen.color = Color.fromHexString("#ED1256");
		Pen.addArc(100@100, 100, 45, 2.3);
		Pen.perform(\fill);
		}
		};
		win.refresh;*/
	}
}