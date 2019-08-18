ConvenientView {

    *crawlerWindow{ |depth, server|
        var initpath;
        var cond = Condition(false);
        var win, sink, sinkColor, depthSetter;
		var crawlerWindowStayOpen = false;
		win = Window.new("ZzZzzzZZ.crawl"/*, resizable: false*/)
		.background_(Color.white)
		.alwaysOnTop_(true)
		.front;
		win.setInnerExtent(260,310);
		StaticText(win, Rect(20, 10, 220, 25)).align_(\center)
		.stringColor_(Color.green)
		.background_(Color.black)
		.string_(" choose init path for crawler ")
		.font_(Font(size:16));
		Button(win, Rect(15,35,230,25))
		.states_([
			["closing when done", Color.white, Color.blue],
			["staying open", Color.black, Color.magenta]
		])
		.font_(Font(size:14))
		.action_({ | state |
			crawlerWindowStayOpen = state.value.asBoolean;
			//"state value: ".post;
			//state.value.asBoolean.postln;
		});
		// receive path
		sink = DragSink(win, Rect(10, 60, 240, 240)).align_(\center);
		sinkColor = Color.white;
		sink.string = "drop folder here and init crawl";
		sink.stringColor_(Color.blue(1.0));
		sink.background_(sinkColor)
		.font_(Font(size:12));
		StaticText(win,Rect(70, 70, 130, 30)).string_("set depth ->");
		depthSetter = TextField(win, Rect(165, 70, 40, 30)).align_(\center);
		depthSetter.string_(depth);
		depthSetter.background_(Color.blue(alpha:0));
		depthSetter.action_{ | str |
			var integerGuard = true;
			str.value.do{ | char | if (char.digit > 9,{integerGuard = false})};
			if (integerGuard, {
				depth = str.value.asInteger;
				{ // Routine GREEN
					2.do{
						var col = 0;
						256.do{
							depthSetter.background_(Color.new255(0,col,0,col.linlin(0,255,255,0)));
							sink.background_(Color.new255(0,col.linlin(0,255,255,100),0,col));
							col = col + 1;
							0.001.wait;
						};
					};
					// back to normal
					sink.background_(sinkColor);
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
				sink.background_(Color.yellow);
				sink.stringColor_(Color.black);
				sink.string = "crawling around";
				5.do{
					5.do{
						0.02.wait;
						sink.string = sink.string+".";
					};
					sink.string = "crawling around";
					0.05.wait;
				};
				sink.stringColor_(Color.green);
				sink.string = "done crawling";
				//"\nother routine autoClose bool is %\n".format(crawlerWindowStayOpen).postln;
				if (crawlerWindowStayOpen == true, {
					1.25.wait;
					// reset, ready for more
					sink.string = "drop folder here and init crawl";
					sink.stringColor_(Color.blue(1.0));
					sink.background_(sinkColor);
				}, {
					0.4.wait;
					win.close
				});
			}.fork(AppClock);
			// gui feeback for humans end
			{ // Routine for the wait Condition
				cond.wait; // wait for dialog
				"\ncrawl:::going to parser".postln;
				// go to parser
				Convenience.prParseFolders(initpath, depth, server);
				"\ncrawl:::done parsing".postln;
			}.fork(AppClock)
		}//.fork(AppClock); // routine for hang yield stuff        
    }
}