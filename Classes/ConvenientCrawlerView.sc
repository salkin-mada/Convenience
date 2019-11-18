ConvenientCrawlerView/* : View*/ {
	classvar fontName = "Roboto";
	// classvar list;

	// *initClass {
	// 	list = Dictionary.new;
	// }

	*open{ |depth, server|
		// user config
		var recursiveGuard = true; // redundant, but protects your mem and swap from insane typ 3e4 depth crawling
		var maxDepth = 99; // guard threshold

		// private config
		var initpath;
		var cond = Condition(false);
		var win, header, stateButton, sink, depthSetter;
		var winW = 340, winH = 340;
		var fontSize = 14, headerFontSize = 18;
		var crawlerWindowStayOpen = false;

		// strings
		var headerString = "choose init path";
		var stateButtonStringSelection = ["close when done", "staying open"];

		// colors
		var backgroundColor = Color.fromHexString("#F06292");
		var headerbackgroundColor = Color.clear;
		var headerStringColor = Color.fromHexString("#311B92");
		var sinkColor = Color.clear;
		var sinkStringColor = Color.fromHexString("#311B92");
		var sinkShapeColor = Color.fromHexString("#ED1256");
		var buttonBackgroundColor = Color.fromHexString("#B39DDB");
		var buttonStringColor = Color.fromHexString("#311B92");

		// efx
		var gradientScroll = 0;
		var gradientDirectionFlag = "up";

		// words
		var defaultSinkString = "drop folder here";

		// placements in win
		var buttonW = 190;
		var buttonH = 25;
		var buttonHorizPlacement = 37;
		var sinkHorizPlacement = 0;
		var sinkTextHorizPlacement = 70;
		var sinkTextW = 270;
		var sinkTextH = 30;

		// geometry
		var dropOvalSize = 50;

		// main
		win = Window.new("crawler", /*resizable: false*//*resizable breaks setInnerExtent*/).acceptsMouseOver_(true)
		.background_(backgroundColor)
		.setInnerExtent(winW, winH)
		.alwaysOnTop_(true);

		win.front; // make pop up

		// header text
		header = StaticText(win, Rect(0, 0, winW, 35)).align_(\center)
		.stringColor_(headerStringColor)
		.background_(headerbackgroundColor)
		.string_(headerString)
		.font_(Font(fontName, headerFontSize, bold: true));

		// state button
		stateButton = Button(win, Rect(winW-buttonW/2, buttonHorizPlacement, buttonW, buttonH))
		.font_(Font(fontName, fontSize))
		.front
		.action_({ | state |
			crawlerWindowStayOpen = state.value.asBoolean;
		}).
		states_([
			[stateButtonStringSelection.[0], buttonStringColor, buttonBackgroundColor],
			[stateButtonStringSelection.[1], buttonStringColor, buttonBackgroundColor]
		]);
		
		// depth setter
		depthSetter = TextField(win, Rect(winW-buttonW/2+buttonW, buttonHorizPlacement, 30, 25))
		.align_(\center)
		.string_(depth)
		.stringColor_(buttonStringColor)
		.background_(buttonBackgroundColor);

		// main focus actions
		win.toFrontAction_({
			win.background_(backgroundColor).alpha_(1.0);
			header.stringColor_(headerStringColor);
			header.background_(headerbackgroundColor);

			if (crawlerWindowStayOpen == false,{
				stateButton.states_([
					[stateButtonStringSelection.[false.asInteger], buttonStringColor, buttonBackgroundColor],
					[stateButtonStringSelection.[true.asInteger], buttonStringColor, buttonBackgroundColor]
				]);
				stateButton.value_(crawlerWindowStayOpen.asInteger); 
			});
			if (crawlerWindowStayOpen == true, {

				stateButton.states_([
					[stateButtonStringSelection.[false.asInteger], buttonStringColor, buttonBackgroundColor],
					[stateButtonStringSelection.[true.asInteger], buttonStringColor, buttonBackgroundColor]
				]);
				stateButton.value_(crawlerWindowStayOpen.asInteger); 	
			});
			
			depthSetter.stringColor_(buttonStringColor);
			depthSetter.background_(buttonBackgroundColor);
			// depth setter above sink please
			depthSetter.front;
		});

		win.endFrontAction_({
			if (win.isClosed.not,{
				win.background_(Color.white).alpha_(0.4);
				header.stringColor_(Color.white);
				header.background_(Color.white);
				stateButton.states_([
					["transparent", Color.white, Color.white],
					["transparent", Color.white, Color.white]
				]);
				depthSetter.stringColor_(Color.white);
				depthSetter.background_(Color.white);
			})
		});


		// SINK
		// receive path
		sink = DragSink(win, Rect(0, sinkHorizPlacement, winW, winH)).acceptsMouseOver_(true)
		.align_(\center)
		.font_(Font(fontName, fontSize))
		.string_(defaultSinkString)
		.stringColor_(sinkStringColor)
		.background_(Color.clear);

		sink.mouseOverAction_({ | view, x, y |

			win.drawFunc = {
				var gradient = this.gradient.mirror;
				var rotate = 0; //(this.gradient.size/2).floor;
				gradient = gradient.rotate(rotate.asInteger);
				// fill gradient
				Pen.addOval(win.view.bounds.insetBy(dropOvalSize));
				Pen.fillRadialGradient(
					win.view.bounds.center,
					win.view.bounds.center, 0, win.bounds.width,
					Color.fromHexString(gradient.[x.linlin(0, winW, 0, (gradient.size-1))]),
					Color.fromHexString(gradient.[y.linlin(0, winH, 0, (gradient.size-1))])
				);
			};
			win.refresh;
		});

		//sink text extra
		/*StaticText(win,Rect(40, sinkTextHorizPlacement, sinkTextW, sinkTextH))
		.string_("zZzzzZ")
		.font_(Font(fontName, fontSize));*/

		//this.sinkShapeHome(win);

		// post do window stuff
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
		
		// actions
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
									Pen.perform([\fill, \stroke].choose);
								}
							};

							win.refresh;
							0.001.wait;
						};
					};
					// back to normal
					sink.background_(Color.clear);
					depthSetter.background_(buttonBackgroundColor);
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
					depthSetter.background_(buttonBackgroundColor)
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
			sink.background_(Color.fromHexString("#3B0BF7"));
			sink.stringColor_(Color.fromHexString("#E1A170"));
			{// Routine for Condition trigger and feedback
				0.4.wait;
				// do real work behind sillyness
				cond.test = true;
				cond.signal;
				sink.background_(Color.fromHexString("#E1A170"));
				sink.stringColor_(Color.fromHexString("#3B0BF7"));
				sink.string = "crawling around";
				5.do{
					5.do{
						0.02.wait;
						sink.string = sink.string+".";
					};
					sink.string = "crawling around";
					0.05.wait;
				};
				sink.background_(Color.fromHexString("#3B0BF7"));
				sink.stringColor_(Color.fromHexString("#E1A170"));
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
				"crawl:::done parsing".postln;
			}.fork(AppClock)
		}
	}

	*gradient {
		var gradient = ["#BFF5FF", "#BEF4FE", "#BDF3FD", "#BDF2FC", "#BCF1FB", "#BCF0FA", "#BBEFF9", "#BAEEF8", "#BAEDF7", "#B9ECF6", "#B9EBF5", "#B8EAF4", "#B7E9F4", "#B7E8F3", "#B6E7F2", "#B6E6F1", "#B5E5F0", "#B5E4EF", "#B4E3EE", "#B3E2ED", "#B3E1EC", "#B2E0EB", "#B2DFEA", "#B1DEEA", "#B0DDE9", "#B0DCE8", "#AFDBE7", "#AFDAE6", "#AED9E5", "#ADD8E4", "#ADD7E3", "#ACD6E2", "#ACD5E1", "#ABD4E0", "#ABD3E0", "#AAD2DF", "#A9D1DE", "#A9D0DD", "#A8CFDC", "#A8CEDB", "#A7CDDA", "#A6CCD9", "#A6CBD8", "#A5CAD7", "#A5C9D6", "#A4C8D6", "#A3C7D5", "#A3C6D4", "#A2C5D3", "#A2C4D2", "#A1C3D1", "#A1C2D0", "#A0C1CF", "#9FC0CE", "#9FBFCD", "#9EBECC", "#9EBDCC", "#9DBCCB", "#9CBBCA", "#9CBAC9", "#9BB9C8", "#9BB8C7", "#9AB7C6", "#99B6C5", "#99B5C4", "#98B4C3", "#98B3C2", "#97B2C2", "#97B1C1", "#96B0C0", "#95AFBF", "#95AEBE", "#94ADBD", "#94ACBC", "#93ABBB", "#92AABA", "#92A9B9", "#91A8B8", "#91A7B8", "#90A6B7", "#8FA5B6", "#8FA4B5", "#8EA3B4", "#8EA2B3", "#8DA1B2", "#8DA0B1", "#8C9FB0", "#8B9EAF", "#8B9DAE", "#8A9CAE", "#8A9BAD", "#899AAC", "#8899AB", "#8898AA", "#8797A9", "#8796A8", "#8695A7", "#8594A6", "#8593A5", "#8492A4", "#8492A4", "#8391A3", "#8390A2", "#828FA1", "#818EA0", "#818D9F", "#808C9E", "#808B9D", "#7F8A9C", "#7E899B", "#7E889A", "#7D879A", "#7D8699", "#7C8598", "#7B8497", "#7B8396", "#7A8295", "#7A8194", "#798093", "#797F92", "#787E91", "#777D90", "#777C90", "#767B8F", "#767A8E", "#75798D", "#74788C", "#74778B", "#73768A", "#737589", "#727488", "#717387", "#717286", "#707186", "#707085", "#6F6F84", "#6F6E83", "#6E6D82", "#6D6C81", "#6D6B80", "#6C6A7F", "#6C697E", "#6B687D", "#6A677C", "#6A667C", "#69657B", "#69647A", "#686379", "#676278", "#676177", "#666076", "#665F75", "#655E74", "#655D73", "#645C72", "#635B72", "#635A71", "#625970", "#62586F", "#61576E", "#60566D", "#60556C", "#5F546B", "#5F536A", "#5E5269", "#5D5168", "#5D5068", "#5C4F67", "#5C4E66", "#5B4D65", "#5B4C64", "#5A4B63", "#594A62", "#594961", "#584860", "#58475F", "#57465E", "#56455E", "#56445D", "#55435C", "#55425B", "#54415A", "#534059", "#533F58", "#523E57", "#523D56", "#513C55", "#513B54", "#503A54", "#4F3953", "#4F3852", "#4E3751", "#4E3650", "#4D354F", "#4C344E", "#4C334D", "#4B324C", "#4B314B", "#4A304A", "#4A304A"];
		^gradient;
	}

}