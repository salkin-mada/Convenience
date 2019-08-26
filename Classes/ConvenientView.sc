ConvenientView {

	*crawlerWindow{ |depth, server|
		// user config
		var recursiveGuard = true; // redundant, but protects your mem and swap from insane typ 3e4 depth crawling
		var maxDepth = 99; // guard threshold

		// private config
		var initpath;
		var cond = Condition(false);
		var win, header, stateButton, sink, depthSetter;
		var winW = 340, winH = 340;
		var fontName = "Monospace", fontSize = 16, headerFontSize = 20;
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
			win.background_(backgroundColor);
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
				win.background_(Color.clear);
				header.stringColor_(Color.clear);
				header.background_(Color.clear);
				stateButton.states_([
					["transparent", Color.clear, Color.clear],
					["transparent", Color.clear, Color.clear]
				]);
				depthSetter.stringColor_(Color.black);
				depthSetter.background_(Color.black);
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
				"\ncrawl:::done parsing".postln;
			}.fork(AppClock)
		}
	}

	*gradient {
		//var gradient = ["#1100FF", "#1403FD", "#1707FC", "#1B0BFA", "#1E0EF9", "#2112F7", "#2516F6", "#2819F5", "#2B1DF3", "#2F21F2", "#3224F0", "#3528EF", "#392CED", "#3C2FEC", "#3F33EB", "#4337E9", "#463AE8", "#493EE6", "#4D42E5", "#5045E4", "#5349E2", "#574DE1", "#5A50DF", "#5D54DE", "#6158DC", "#645CDB", "#675FDA", "#6B63D8", "#6E67D7", "#716AD5", "#756ED4", "#7872D2", "#7B75D1", "#7F79D0", "#827DCE", "#8580CD", "#8984CB", "#8C88CA", "#8F8BC9", "#938FC7", "#9693C6", "#9996C4", "#9D9AC3", "#A09EC1", "#A3A1C0", "#A7A5BF", "#AAA9BD", "#ADACBC", "#B1B0BA", "#B4B4B9", "#B8B8B8", "#B4B9B7", "#B0BAB6", "#ACBCB6", "#A9BDB5", "#A5BFB5", "#A1C0B4", "#9EC1B4", "#9AC3B3", "#96C4B3", "#93C6B2", "#8FC7B2", "#8BC9B1", "#88CAB0", "#84CBB0", "#80CDAF", "#7DCEAF", "#79D0AE", "#75D1AE", "#72D2AD", "#6ED4AD", "#6AD5AC", "#67D7AC", "#63D8AB", "#5FDAAB", "#5CDBAA", "#58DCA9", "#54DEA9", "#50DFA8", "#4DE1A8", "#49E2A7", "#45E4A7", "#42E5A6", "#3EE6A6", "#3AE8A5", "#37E9A5", "#33EBA4", "#2FECA4", "#2CEDA3", "#28EFA2", "#24F0A2", "#21F2A1", "#1DF3A1", "#19F5A0", "#16F6A0", "#12F79F", "#0EF99F", "#0BFA9E", "#07FC9E", "#03FD9D", "#00FF9D", "#05FE99", "#0AFE96", "#0FFD93", "#14FD90", "#19FC8D", "#1EFC8A", "#23FB87", "#28FB83", "#2DFA80", "#33FA7D", "#38F97A", "#3DF977", "#42F874", "#47F871", "#4CF76D", "#51F76A", "#56F667", "#5BF664", "#60F561", "#66F55E", "#6BF45B", "#70F457", "#75F354", "#7AF351", "#7FF24E", "#84F24B", "#89F148", "#8EF145", "#93F041", "#99F03E", "#9EEF3B", "#A3EF38", "#A8EE35", "#ADEE32", "#B2ED2F", "#B7ED2B", "#BCEC28", "#C1EC25", "#C6EB22", "#CCEB1F", "#D1EA1C", "#D6EA19", "#DBE915", "#E0E912", "#E5E80F", "#EAE80C", "#EFE709", "#F4E706", "#F9E603", "#FFE600", "#FFE104", "#FFDC08", "#FFD70C", "#FFD310", "#FFCE14", "#FFC918", "#FFC51C", "#FFC020", "#FFBB24", "#FFB728", "#FFB22C", "#FFAD30", "#FFA835", "#FFA439", "#FF9F3D", "#FF9A41", "#FF9645", "#FF9149", "#FF8C4D", "#FF8851", "#FF8355", "#FF7E59", "#FF7A5D", "#FF7561", "#FF7066", "#FF6B6A", "#FF676E", "#FF6272", "#FF5D76", "#FF597A", "#FF547E", "#FF4F82", "#FF4B86", "#FF468A", "#FF418E", "#FF3D92", "#FF3897", "#FF339B", "#FF2E9F", "#FF2AA3", "#FF25A7", "#FF20AB", "#FF1CAF", "#FF17B3", "#FF12B7", "#FF0EBB", "#FF09BF", "#FF04C3", "#FF00C8"];
		var gradient = ["#BFF5FF", "#BEF4FE", "#BDF3FD", "#BDF2FC", "#BCF1FB", "#BCF0FA", "#BBEFF9", "#BAEEF8", "#BAEDF7", "#B9ECF6", "#B9EBF5", "#B8EAF4", "#B7E9F4", "#B7E8F3", "#B6E7F2", "#B6E6F1", "#B5E5F0", "#B5E4EF", "#B4E3EE", "#B3E2ED", "#B3E1EC", "#B2E0EB", "#B2DFEA", "#B1DEEA", "#B0DDE9", "#B0DCE8", "#AFDBE7", "#AFDAE6", "#AED9E5", "#ADD8E4", "#ADD7E3", "#ACD6E2", "#ACD5E1", "#ABD4E0", "#ABD3E0", "#AAD2DF", "#A9D1DE", "#A9D0DD", "#A8CFDC", "#A8CEDB", "#A7CDDA", "#A6CCD9", "#A6CBD8", "#A5CAD7", "#A5C9D6", "#A4C8D6", "#A3C7D5", "#A3C6D4", "#A2C5D3", "#A2C4D2", "#A1C3D1", "#A1C2D0", "#A0C1CF", "#9FC0CE", "#9FBFCD", "#9EBECC", "#9EBDCC", "#9DBCCB", "#9CBBCA", "#9CBAC9", "#9BB9C8", "#9BB8C7", "#9AB7C6", "#99B6C5", "#99B5C4", "#98B4C3", "#98B3C2", "#97B2C2", "#97B1C1", "#96B0C0", "#95AFBF", "#95AEBE", "#94ADBD", "#94ACBC", "#93ABBB", "#92AABA", "#92A9B9", "#91A8B8", "#91A7B8", "#90A6B7", "#8FA5B6", "#8FA4B5", "#8EA3B4", "#8EA2B3", "#8DA1B2", "#8DA0B1", "#8C9FB0", "#8B9EAF", "#8B9DAE", "#8A9CAE", "#8A9BAD", "#899AAC", "#8899AB", "#8898AA", "#8797A9", "#8796A8", "#8695A7", "#8594A6", "#8593A5", "#8492A4", "#8492A4", "#8391A3", "#8390A2", "#828FA1", "#818EA0", "#818D9F", "#808C9E", "#808B9D", "#7F8A9C", "#7E899B", "#7E889A", "#7D879A", "#7D8699", "#7C8598", "#7B8497", "#7B8396", "#7A8295", "#7A8194", "#798093", "#797F92", "#787E91", "#777D90", "#777C90", "#767B8F", "#767A8E", "#75798D", "#74788C", "#74778B", "#73768A", "#737589", "#727488", "#717387", "#717286", "#707186", "#707085", "#6F6F84", "#6F6E83", "#6E6D82", "#6D6C81", "#6D6B80", "#6C6A7F", "#6C697E", "#6B687D", "#6A677C", "#6A667C", "#69657B", "#69647A", "#686379", "#676278", "#676177", "#666076", "#665F75", "#655E74", "#655D73", "#645C72", "#635B72", "#635A71", "#625970", "#62586F", "#61576E", "#60566D", "#60556C", "#5F546B", "#5F536A", "#5E5269", "#5D5168", "#5D5068", "#5C4F67", "#5C4E66", "#5B4D65", "#5B4C64", "#5A4B63", "#594A62", "#594961", "#584860", "#58475F", "#57465E", "#56455E", "#56445D", "#55435C", "#55425B", "#54415A", "#534059", "#533F58", "#523E57", "#523D56", "#513C55", "#513B54", "#503A54", "#4F3953", "#4F3852", "#4E3751", "#4E3650", "#4D354F", "#4C344E", "#4C334D", "#4B324C", "#4B314B", "#4A304A", "#4A304A"];
		^gradient;
	}

	// roll'n palceholdin' bla bla
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