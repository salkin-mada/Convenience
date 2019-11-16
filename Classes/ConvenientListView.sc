ConvenientListView : View {
    classvar fontName = "Roboto";
    classvar win, dragSource;
    classvar listWindowOpen = false;
	classvar winW = 200, winH = 240;
	classvar dragSourceHeight = 22;
	classvar fontSize = 14;

    *gradient {
		var gradient = ["#BFF5FF", "#BEF4FE", "#BDF3FD", "#BDF2FC", "#BCF1FB", "#BCF0FA", "#BBEFF9", "#BAEEF8", "#BAEDF7", "#B9ECF6", "#B9EBF5", "#B8EAF4", "#B7E9F4", "#B7E8F3", "#B6E7F2", "#B6E6F1", "#B5E5F0", "#B5E4EF", "#B4E3EE", "#B3E2ED", "#B3E1EC", "#B2E0EB", "#B2DFEA", "#B1DEEA", "#B0DDE9", "#B0DCE8", "#AFDBE7", "#AFDAE6", "#AED9E5", "#ADD8E4", "#ADD7E3", "#ACD6E2", "#ACD5E1", "#ABD4E0", "#ABD3E0", "#AAD2DF", "#A9D1DE", "#A9D0DD", "#A8CFDC", "#A8CEDB", "#A7CDDA", "#A6CCD9", "#A6CBD8", "#A5CAD7", "#A5C9D6", "#A4C8D6", "#A3C7D5", "#A3C6D4", "#A2C5D3", "#A2C4D2", "#A1C3D1", "#A1C2D0", "#A0C1CF", "#9FC0CE", "#9FBFCD", "#9EBECC", "#9EBDCC", "#9DBCCB", "#9CBBCA", "#9CBAC9", "#9BB9C8", "#9BB8C7", "#9AB7C6", "#99B6C5", "#99B5C4", "#98B4C3", "#98B3C2", "#97B2C2", "#97B1C1", "#96B0C0", "#95AFBF", "#95AEBE", "#94ADBD", "#94ACBC", "#93ABBB", "#92AABA", "#92A9B9", "#91A8B8", "#91A7B8", "#90A6B7", "#8FA5B6", "#8FA4B5", "#8EA3B4", "#8EA2B3", "#8DA1B2", "#8DA0B1", "#8C9FB0", "#8B9EAF", "#8B9DAE", "#8A9CAE", "#8A9BAD", "#899AAC", "#8899AB", "#8898AA", "#8797A9", "#8796A8", "#8695A7", "#8594A6", "#8593A5", "#8492A4", "#8492A4", "#8391A3", "#8390A2", "#828FA1", "#818EA0", "#818D9F", "#808C9E", "#808B9D", "#7F8A9C", "#7E899B", "#7E889A", "#7D879A", "#7D8699", "#7C8598", "#7B8497", "#7B8396", "#7A8295", "#7A8194", "#798093", "#797F92", "#787E91", "#777D90", "#777C90", "#767B8F", "#767A8E", "#75798D", "#74788C", "#74778B", "#73768A", "#737589", "#727488", "#717387", "#717286", "#707186", "#707085", "#6F6F84", "#6F6E83", "#6E6D82", "#6D6C81", "#6D6B80", "#6C6A7F", "#6C697E", "#6B687D", "#6A677C", "#6A667C", "#69657B", "#69647A", "#686379", "#676278", "#676177", "#666076", "#665F75", "#655E74", "#655D73", "#645C72", "#635B72", "#635A71", "#625970", "#62586F", "#61576E", "#60566D", "#60556C", "#5F546B", "#5F536A", "#5E5269", "#5D5168", "#5D5068", "#5C4F67", "#5C4E66", "#5B4D65", "#5B4C64", "#5A4B63", "#594A62", "#594961", "#584860", "#58475F", "#57465E", "#56455E", "#56445D", "#55435C", "#55425B", "#54415A", "#534059", "#533F58", "#523E57", "#523D56", "#513C55", "#513B54", "#503A54", "#4F3953", "#4F3852", "#4E3751", "#4E3650", "#4D354F", "#4C344E", "#4C334D", "#4B324C", "#4B314B", "#4A304A", "#4A304A"];
		^gradient;
	}

	*open {
		var backgroundColor = Color.fromHexString("#808B9D");

		if (listWindowOpen.not, {
			// main
			win = Window.new("convenient list", Rect(500,400,winW,winH), scroll: true, resizable: false)
			.acceptsMouseOver_(true)
			.background_(backgroundColor)
			.alwaysOnTop_(true)
            .onClose_({listWindowOpen = false});

			win.view.decorator = FlowLayout(win.view.bounds);
			win.front; // make pop up

			// main focus actions
			win.toFrontAction_({
				win.background_(backgroundColor).alpha_(1.0)
			});

			win.endFrontAction_({
				if (win.isClosed.not,{
					win.background_(Color.white).alpha_(0.4)
				})
			});

			this.makeList(win);

			// buffers.keysValuesDo { |folderName, buffers|
			// 	"% [%]".format(folderName, buffers.size).postln
			// };

			listWindowOpen = true;
		})
	}

    *makeList {
        var dragSourceStringColor = Color.fromHexString("#4A304A");
        Convenience.bufferKeys.do{ | key |
				var gradientStartPoint = Point(200.rand,200.rand);
				var gradientEndPoint = Point(200.rand,200.rand);
                var dragSource;
				dragSource = DragSource(win, Rect(0,0,winW-10,dragSourceHeight))
				.background_(Color.clear)
				.stringColor_(dragSourceStringColor)
				.object_("\\"++"%".format(key)).align_(\center)
				.font_(Font(fontName, fontSize, bold: true))
				.mouseOverAction_({ | view, x, y |
					// win.drawFunc = {
					// 	var gradient = this.gradient.mirror;
					// 	// fill gradient
					// 	//Pen.addRect(40, 50, 250, 500); // not working
					// 	Pen.addRect(win.view.bounds.insetBy(0));
					// 	Pen.fillAxialGradient(
					// 		gradientStartPoint,
					// 		gradientEndPoint,
					// 		Color.fromHexString(gradient.[x.linlin(0, winW, 0, (gradient.size-1))]),
					// 		Color.fromHexString(gradient.[y.linlin(0, winH, 0, (gradient.size-1))])
					// 	);
					// };
                	//dragSource.font_(Font(fontName, size: fontSize+2.rand, bold: 2.rand.asBoolean));
					dragSource.background_(Color.new(Color.rand.red, 0.5, 0.5));
					win.refresh;
				})
			};
			//buffers.size
    }

	*update{
		if (listWindowOpen, {
			this.prClear;
			this.makeList(win);
		})
	}

	*prClear{
		win.view.children.do{ | child | child.remove };
		win.view.decorator.reset;
	}
}