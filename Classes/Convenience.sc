Convenience {
	classvar <dir, <buffers, <folderPaths;
	classvar loadFn;

	// config begin
	classvar loadSynths = true;
	// config end

	const <supportedExtensions = #[\wav, \wave, \aif, \aiff, \flac];

	*initClass {
		buffers = Dictionary.new;
		folderPaths = Dictionary.new;
		loadFn = #{ | server | Convenience.prPipeFoldersToLoadFunc(server) };
		this.prAddEventType;
		"\nConvenience is possible".postln;
	}

	*p { | name, type=\Convenience, out = 0, folder, index = 1, dur = 8, stretch = 1.0,
		pos = 0, loop = 0, rate = 1, degree = 0, octave = 3, root = 0, scale,
		cutoff = 22e3, bass = 0, pan = 0, spread = 0, amp = 0.5, attack = 0.1,
		decay = 0.5, sustain=1.0, release = 0.5, tempo, tuningOnOff = 0,
		pattack = 0.0, pdecay = 0.0, psustain=1.0, prelease = 9e3,
		basefreq = 440, fftOnOff = 0, binRange = 20, pitchShiftOnOff = 0, pitchRatio = 1.0, formantRatio = 1.0 |

		//var return;

		if (ConvenientDefinitions.synthsBuild, {
			if(name.isNil,{"needs a key aka name, please".throw; ^nil});

			// if folder is unspecified in Convenience.p func
			/*if (folder.isNil, {
				if(Convenience.folders.asArray[0].isNil.not, {
					folder = Convenience.folders.asArray[0];
					//"choosing first bufferGroup".postln;
				}, {Error("Conveience:: no buffers available").throw; ^nil})
			});*/
			// if queried folder does not exist
			// if (folder.isKindOf(Pattern).not, { // <-- not good, should be dynamic dispatched?
			// 	// polymorphic.. feelings.
			// 	if (Convenience.buffers.includesKey(folder).not, {
			// 		"cant find queried folder: %".format(folder).postln;
			// 		if(Convenience.folders.asArray[0].isNil.not, {
			// 			folder = Convenience.folders.asArray[0];
			// 			"replacing with: %".format(folder).postln;
			// 		}, {Error("Conveience:: no buffers available").throw; ^nil})
			// 	})
			// }, { /*folder received a pattern*/ });

			// if scale is not set choose classic chromatic
			if(scale.isNil, {
				scale = Scale.chromatic;
			});

			// decide what clock to use
			// check if Utopia is in Class library
			if ( Main.packages.asDict.includesKey(\Utopia) == true, {
				//"\n\tConvenience:: Utopia is possible\n".postln;
				if (tempo.class == BeaconClock, {
					// great do nothing
					//"\ttempo is BeaconClock controlled".postln
				}, {
					tempo = TempoClock(tempo);
					//"\tusing tempoclock".postln;
				})
			}, {
				"\tConvenience:: suggests to install Utopia Quark".postln;
				tempo = TempoClock(tempo);
			});


			Pdef(name,
				Pbind(
					\type, type,
					\fftOnOff, fftOnOff,
					\pitchShiftOnOff, pitchShiftOnOff,
					\tuningOnOff, tuningOnOff,
					\basefreq, basefreq,
					\out, out,
					\folder, folder,
					\index, index,
					\dur, dur,
					\stretch, stretch,
					\pos, pos,
					\loop, loop,
					\rate, rate, \pattack, pattack, \pdecay, pdecay, \psustain, psustain, \prelease, prelease,
					\degree, degree,
					\octave, octave,
					\root, root,
					\scale, scale,
					\cutoff, cutoff,
					\bass, bass,
					\pan, pan,
					\spread, spread,
					\amp, amp,
					\attack, attack,
					\decay, decay,
					\sustain, sustain,
					\release, release,
					\binRange, binRange,
					\pitchRatio, pitchRatio,
					\formantRatio, formantRatio
				);
			).play(tempo);
		}, {
			"Convenience:: synths not added".postln;
		});
	}

	*s { | name |
		Pdef(name).stop
	}

	*crawl { | initpath, depth = 0, server |

		// if server is nil set to default
		server = server ? Server.default;

		// if no path is specified open crawl drag'n drop window
		if (initpath.isNil, {
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
						2.0.wait;
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
					this.prParseFolders(initpath, depth, server);
					"\ncrawl:::done parsing".postln;
				}.fork(AppClock)

			}//.fork(AppClock); // routine for hang yield stuff

		}, {
			// NO WINDOW USAGE
			// initpath was set when crawl method was called
			// going directly to parsing!
			"going directly, no gui".postln;
			this.prParseFolders(initpath, depth, server)
		});

		// load synths on/off
		if (loadSynths == true, {
			// add synths if not already done
			if (ConvenientDefinitions.synthsBuild.not,{
				ConvenientDefinitions.addSynths;
			});
		});

	}

	*prParseFolders{ | initpath, depth = 0, server |
		var initPathDepthCount = 0;

		//"\n__prParseFolders__".post;
		//"\n\tinitpath: %".format(initpath).post;
		//"\n\tdepth: %\n".format(depth).postln;

		server = server ? Server.default;

		dir = initpath;

		// count init depth
		PathName(initpath).pathOnly.do{ | char |
			// here we should have a guard func that removes the last slash in initpath if it was given
			// aka ~/Desktop/soundFiles instead of ~/Desktop/soundFiles
			// for now if user writes a path that ends with "/" this breaks the depth control math/iteration
			if (char == $/, {initPathDepthCount = initPathDepthCount + 1;})
		};
		//"initPathDepthCount: %".format(initPathDepthCount).postln;

		// take init depth into account aka how many slashed in initpath
		depth = depth + initPathDepthCount;
		//"after init path depth addition/offset, depth is: %".format(depth).postln;

		PathName(initpath).filesDo{ | item |
			var loadFolderFlag;
			var depthCounter = 0;

			// folder depth control
			item.pathOnly.do{ | char |
				// linux      /    windows    \\
				if (char == ($/) || (char == ($\\)), {
					if (depthCounter <= depth, {
						loadFolderFlag = true;
						depthCounter = depthCounter+1;
					}, { loadFolderFlag = false;
					});
				})
			};

			//"parser checking: %".format(item.pathOnly).postln;
			//"after depth control loadFolderFlag is %".format(loadFolderFlag).postln;

			if (loadFolderFlag == true, {
				var folderKey;
				folderKey = this.prKeyify(item.folderName);
				// add to folderPaths if not already present
				if (folderPaths.includesKey(folderKey).not, {
					folderPaths.add(folderKey -> item.pathOnly.asSymbol);
					//"added % to folderPaths".format(folderKey).postln;
				}, {
					// folder already added to folderPaths
					/*"folder % included in folderPaths will not be added again".format(
					item.pathOnly.asSymbol
					).postln;*/
				});
			});
			//"parser_iteration".postln;
		};

		//folderPaths.keysDo{ | item |
		//"\n\t__prParseFolders__folderPath: %\n".format(item).postln
		//};
		if (folderPaths.isEmpty, {Error("\n\n\n\tfolderPaths is EMPTY!\n\n\n\n").throw});

		// stage work for boot up
		ServerBoot.add(loadFn, server);
		// if server is running create rightaway
		if (server.serverRunning) {
			this.prPipeFoldersToLoadFunc(server)
		};
	}

	*prPipeFoldersToLoadFunc{ | server |

		/*folderPaths.keysDo{|item|
		"\n\n\t***prPipeFoldersToLoadFunc**\nfolderPath: %\n".format(item).postln
		};*/

		if (folderPaths.isEmpty.not,{
			folderPaths.keysValuesDo{ | key, path |
				this.load(path.asString, server);
			};
			"done piping".postln;
			// update folderPaths to be even with loaded buffers
			folderPaths.keysDo{ | key |
				//key.postln;
				// clean up folderPath dir after loading
				if(buffers.includesKey(key).not,{
					folderPaths.removeAt(key);
					//"removed % from folderPaths".format(key).postln;
				}
			)};

		}, {Error("no folderPaths ?! possibly init/root path is a file").throw});
	}

	*load { | path, server |
		var folder = PathName(path);
		var files, loadedBuffers, folderKey;
		folderKey = this.prKeyify(folder.folderName);

		server = server ? Server.default;

		if (buffers.includesKey(folderKey).not, {

			// check header only return item if it is supported
			files = folder.entries.select { | file |
				supportedExtensions.includes(file.extension.toLower.asSymbol)
			};
			//"files: %".format(files).postln;

			// load files into buffers
			loadedBuffers = files.collect { | file |
				Buffer.readChannel(server, file.fullPath;, channels: [0]).normalize(0.99);
			};

			//"\n\t loadedBuffers from folder: % --> %".format(folder.folderName,loadedBuffers).postln;
			//server.sync; // danger danger only working when used before boot use update here instead?
			//"loading into memory, hang on".postln;

			// add loadedBuffers to dictionary with key from common folder
			if (loadedBuffers.isEmpty.not, {
				if (buffers.includesKey(folderKey).not, {
					"added new folder as key: %".format(folderKey).postln;
					//  add and remove spaces in folder name
					buffers.add(folderKey -> loadedBuffers);
				})
			}, {
				//"no soundfiles in : %, skipped".format(folder.folderName).postln;
				// update folderPaths
				// folderPaths.removeAt(folderKey);
			});
		}, {"folder % already loaded".format(folderKey).postln});
	}

	*free { | folder, server |
		// free all no folder specified
		if (folder.isNil,{
			if (buffers.isEmpty.not, {
				this.prFreeBuffers;
				this.prClearFolderPaths; // also reset parser -> empty dictionary of folder paths
				server = server ? Server.default;
				ServerBoot.remove(loadFn, server);
				"all buffers freed".postln;
			}, {
				"no buffers to free".postln
			})
		}, { // free only specified folder
			if (buffers.includesKey(folder), {
				buffers.keysValuesDo { | item |
					if (item == folder, {
						item.do { | buffer |
							if (buffer.isNil.not, {
								buffer.free;
							})
						}
					});
				};
				buffers.removeAt(folder);
				"buffers freed and removed".postln;
			});
			if (folderPaths.includesKey(folder), {
				folderPaths.removeAt(folder);
				"folder paths removed".postln;
			})
		});
	}

	*clearFolderPathsDict {
		this.prClearFolderPaths;
	}
	*randomFolder {
		^this.folderNum(this.folders.size.rand)
	}

	*prClearFolderPaths {
		folderPaths.clear
	}

	* addSynths {
		if (ConvenientDefinitions.synthsBuild.not,{
			ConvenientDefinitions.addSynths;
		});
	}

	*get { | folder, index |
	
		if (buffers.notEmpty, {
			
			var bufferGroup;

			// if folder is unspecified
			if (folder.isNil, {
				if(Convenience.folders.asArray[0].isNil.not, {
					folder = Convenience.folders.asArray[0];
				}, {
					Error("Conveience::*get::
					folder is unspecified, which is okay
					but *get cant find a folder to use").throw;
					^nil
				})
			});
			// if queried folder does not exist
			if (Convenience.buffers.includesKey(folder).not, {
				"*get:: cant find queried folder: %".format(folder).postln;
				if(Convenience.folders.asArray[0].isNil.not, {
					folder = Convenience.folders.asArray[0];
					"*get::replacing with: %".format(folder).postln;
				}, {
					Error("Conveience::*get:: 
					user asking for folder which is not there, 
					but *get cant find another folder to replace it with").throw;
					^nil
				})
			});

			bufferGroup = buffers[folder.asSymbol];

			// if index is unspecified
			if (index.isNil, {index = 0});
			// always get a buffer for user
			if (bufferGroup.isNil.not, {
				index = index % bufferGroup.size;
				^bufferGroup[index]
			});
		}, {
			Error("Conveience::*get:: buffers is empty").throw;
			^nil
		});
	}

	*folders {
		^buffers.keys
	}

	*size {
		^this.buffers.values.collect{|i|i.size}.sum
	}

	// only think in integers
	*folderNum { | index |
		var folder;

		if (buffers.isNil.not) {
			^buffers.keys.asArray[index.wrap(0,buffers.keys.size-1)]
		}

		/*if (buffers.isNil.not) {
		var bufferGroup;

		bufferGroup = buffers[folder.asSymbol];

		if (bufList.isNil.not) {
		index = index % bufList.size;
		"index er % bufList size er %".format(index, bufList.size).postln;
		^bufList[index]
		}
		};*/
		^nil
	}

	*files {
		^buffers.do { |folderName, buffers|
			"% %".format(folderName, buffers.size).postln
		}
	}

	*list {
		^buffers.keysValuesDo { |folderName, buffers|
			"% [%]".format(folderName, buffers.size).postln
		}
	}

	*prFreeBuffers {
		buffers.do { | folders |
			folders.do { | buf |
				if (buf.isNil.not) {
					buf.free
				}
			}
		};
		buffers.clear;
	}

	*prKeyify { | input |
		var result;
		result = input
		.replace(($ ),"_")
		.replace(($-),"_")
		.replace(($,),"")
		.replace(($+),"")
		.replace("æ","ae")
		.replace("ø","o")
		.replace("å","aa")
		.asSymbol;
		^result
	}

	*prAddEventType {
		Event.addEventType(\Convenience, {
			var numChannels, scaling, fft, pitchshift;

			if (~buf.isNil) {
				var folder = ~folder;
				if (folder.isNil.not) {
					var index = ~index ? 0;
					~buf = Convenience.get(folder, index)
				} {
					// pair split if folder is nil
					var sample = ~sample;
					if (sample.isNil.not) {
						var pair, folder, index;
						pair = sample.split($:);
						folder = pair[0].asSymbol;
						index = if (pair.size == 2) { pair[1].asInt } { 0 };
						~buf = Convenience.get(folder, index)
					}
				}
			};

			numChannels = ~buf.bufnum.numChannels;

			scaling = ~tuningOnOff;
			if(scaling.isNil) {scaling = 0};
			fft = ~fftOnOff;
			if(fft.isNil) {fft = 0};
			pitchshift = ~pitchShiftOnOff;
			if(pitchshift.isNil) {pitchshift = 0};

			case
			{fft == 1 and: pitchshift == 0 and: scaling == 0} {
				~instrument = \ConvenienceBufBins;
				//"FFT".postln;
			}
			{fft == 1 and: pitchshift == 0 and: scaling == 1} {
				~instrument = \ConvenienceBufBinsScale;
				//"FFT+SCALING".postln;
			}
			{pitchshift == 1 and: scaling == 0} {
				~instrument = \ConveniencePitchShift;
				//"PITCHSHIFT".postln;
			}
			{pitchshift == 1 and: scaling == 1} {
				~instrument = \ConveniencePitchShiftScale;
				//"PITCHSHIFT+SCALING".postln;
			}
			{scaling == 1 and: fft == 0 and: pitchshift == 0} {
				//"SCALING -- ".post;
				switch(numChannels,
					1, {
						~instrument = \ConvenienceMonoScale;
						//"mono".postln;
					},
					2, {
						~instrument = \ConvenienceStereoScale;
						//"stereo".postln;
					},
					{
						~instrument = \ConvenienceMonoScale;
						//"mono-default".postln;
					}
				);

			}
			{
				//"NORMALES -- ".post;
				switch(numChannels,
					1, {
						~instrument = \ConvenienceMono;
						//"mono".postln;
					},
					2, {
						~instrument = \ConvenienceStereo;
						//"stereo".postln;
					},
					{
						~instrument = \ConvenienceMono;
						//"mono-default".postln;
					}
				);
			};

			~type = \note;
			~bufnum = ~buf.bufnum;
			currentEnvironment.play
		});
	}

	// fft method for selecting filter bands
	// thanks to Bálint Laczkó
	*buckets {| frame = 1024, numBands = 4, band = 0 |
		var result, coeff, binLow, binHigh;

		//snap frame to power of 2
		frame = 2**frame.log2.round;

		//numBands should not be lower than 1
		if(numBands < 1, {numBands = 1}, {numBands});

		//selected band index clipped into sensible range
		band = band.clip(0, numBands -1);

		//the coefficient for calculating logarithmically scaled bands
		//(the same equation as used for equal temperament)
		coeff = (frame / 2)**(1 / numBands);

		//calculate the "bin-range" of the selected band
		binLow = ((coeff**band).round - 1).clip(0, (frame / 2) - 1);
		if(band == (numBands - 1), {
			binHigh = ((coeff**(band + 1)).round - 1).clip(binLow, (frame / 2) - 1)
		}, {binHigh = ((coeff**(band + 1)).round - 2).clip(binLow, (frame / 2) - 1)
		});
		result = [binLow, binHigh];
		//return the bin-range in an array
		^result
	}


}