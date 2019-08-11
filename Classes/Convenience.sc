// implement protector condition
// ihværksæt beskyttelses tilstand
// + Scale.names[32.wrap(0, Scale.names.size-1)].asString = method --> Convenience.scaleNum
// ++ config setup for hvornår tilstanden skal slå ind / ændres
// +++tillæg en beskytter for at loade en mappe med samme navn i en anden sti...?

Convenience {
	classvar <dir, <buffers, <folderPaths;
	classvar loadFn;
	classvar synthsBuild = false;

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
		basefreq = 440, fftOnOff = 0, binRange = 20 |

		//var return;

		if (synthsBuild, {
			if(name.isNil,{"needs a key aka name, please".throw; ^nil});

			// if folder is unspecified in Convenience.p func
			if (folder.isNil, {
				if(Convenience.folders.asArray[0].isNil.not, {
					folder = Convenience.folders.asArray[0];
					"choosing first bufferGroup".postln;
				}, {Error("not init corr no folder avai").throw; ^nil})
			});

			// if scale is not set choose classic chromatic
			if(scale.isNil, {
				scale = Scale.chromatic;
			});

			"tempo pre ****** %".format(tempo.class).postln;

			// check if Utopia is in Class library
			if ( Main.packages.asDict.includesKey(\Utopia) == true, {
				"\n\tConvenience:: Utopia is possible\n".postln;
				if (tempo.class == BeaconClock, {
					// great do nothing
					"\ttempo is BeaconClock controlled".postln
				}, {
					tempo = TempoClock(tempo);
					"\tusing tempoclock".postln;
				})
			}, {
				"Convenience suggests to install Utopia Quark".postln;
				tempo = TempoClock(tempo);
			});


			Pdef(name,
				Pbind(
					\type, type,
					\fftOnOff, fftOnOff,
					\tuningOnOff, tuningOnOff,
					\basefreq, basefreq,
					\out, out,
					\folder, folder,
					// \folder, Pfunc({ | folder |
					// 	// id called folder does not exist, choose an existing one
					// 	if (Convenience.buffers.includesKey(folder).not, {
					// 		"cant find queried folder: %".format(folder).postln;
					// 		if(Convenience.folders.asArray[0].isNil.not, {
					// 		folder = Convenience.folders.asArray[0];
					// 		"replacing with: %".format(folder).postln;
					// 		}, {Error("not init corr no folder avai").throw; ^nil})
					// 	});
					// }),
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
					\binRange, binRange
				);
			).play(tempo);
		}, {
			"Convenience::synths not added".postln;
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
			if (synthsBuild.not,{
				this.prAddSynthDefinitions;
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
				folderKey = item.folderName
				.replace(($ ),"_")
				.replace(($-),"_")
				.replace(($,),"")
				.replace(($+),"")
				/*		.replace(($æ),"ae")
				.replace(($ø),"o")
				.replace(($å),"aa")*/
				.asSymbol;
				//folderKey = item.folderName.replace(($ ),"_").replace(($-),"_").replace(($,),"").replace(($+),"").asSymbol;
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
				if(buffers.includesKey(key).not,{
					folderPaths.removeAt(key);
					"removed % from folderPaths".format(key).postln;
				}
			)};

		}, {Error("no folderPaths ?! possibly init/root path is a file").throw});
	}

	*load { | path, server |
		var folder = PathName(path);
		var files, loadedBuffers, folderKey;
		folderKey = folder.folderName
		.replace(($ ),"_")
		.replace(($-),"_")
		.replace(($,),"")
		.replace(($+),"")
		/*		.replace(($æ),"ae")
		.replace(($ø),"o")
		.replace(($å),"aa")*/
		.asSymbol;
		//folderKey = folder.folderName.replace(($ ),"_").replace(($-),"_").replace(($,),"").asSymbol;

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
					"added new folder: % as key %".format(folder.folderName,folderKey).postln;
					//  add and remove spaces in folder name
					buffers.add(folderKey -> loadedBuffers);
				})
			}, {
				"no soundfiles in : %, skipped".format(folder.folderName).postln;
				// update folderPaths
				//folderPaths.removeAt(folderKey);
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
		if (synthsBuild.not,{
			this.prAddSynthDefinitions;
		});
	}

	*get { | folder, index |
		if (buffers.isNil.not) {
			var bufferGroup = buffers[folder.asSymbol];
			if (bufferGroup.isNil.not) {
				index = index % bufferGroup.size;
				^bufferGroup[index]
			}
		};
		^nil
	}

	*folders {
		^buffers.keys
	}

	*size {
		^this.buffers.values.collect{|i|i.size}.sum
	}

	// only think i integers
	*folderNum { |index|
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

	*prAddSynthDefinitions {
		"building synth definitions".postln;
		/*	  --------------------------------------  */
		/*	  rate style							 */
		/*	  ------------------------------------- */
		SynthDef(\ConvenienceMono, {
			|
			bufnum, out = 0, loop = 0, rate = 1, spread = 1, pan = 0, amp = 0.5,
			attack = 0.01, decay = 0.5, sustain = 0.5, release = 1.0, pos = 0,
			pattack = 0.0, pdecay = 0.0, psustain = 1.0, prelease = 100.0,
			// long hack.. when penv is not in use, penv should always be longer than env
			gate = 1, cutoff = 22e3, bass = 0.0
			|
			var sig, key, frames, env, penv, file;
			penv = EnvGen.ar(Env.adsr(pattack, pdecay, psustain, prelease), gate);
			frames = BufFrames.kr(bufnum);
			sig = ConvenienceBufferPlay.ar(
				1,
				bufnum,
				rate*BufRateScale.kr(bufnum)*penv,
				1,
				pos*frames,
				loop: loop
			);
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate);
			FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
			sig = LPF.ar(sig, cutoff);
			sig = sig + (LPF.ar(sig, 100, bass));
			sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
			sig = LeakDC.ar(sig);
			Out.ar(out, (sig*env));
		}).add;

		SynthDef(\ConvenienceStereo, {
			|
			bufnum, out = 0, loop = 0, rate = 1, spread = 1, pan = 0, amp = 0.5,
			attack = 0.01, decay = 0.5, sustain = 0.5, release = 1.0, pos = 0,
			gate = 1, cutoff = 22e3, bass = 0.0
			|
			var sig, key, frames, env, file;
			frames = BufFrames.kr(bufnum);
			sig = ConvenienceBufferPlay.ar(
				2,
				bufnum,
				rate*BufRateScale.kr(bufnum),
				1,
				pos*frames,
				loop: loop
			);
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate);
			FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
			sig = LPF.ar(sig, cutoff);
			sig = sig + (LPF.ar(sig, 100, bass));
			sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
			sig = LeakDC.ar(sig);
			Out.ar(out, (sig*env));
		}).add;

		/*	  --------------------------------------  */
		/*	  for scaling, assuming samples are tuned */
		/*	  ------------------------------------- */
		SynthDef(\ConvenienceMonoScale, {
			|
			bufnum, out = 0, loop = 0, spread = 1, pan = 0, amp = 0.5,
			attack = 0.01, decay = 0.5, sustain = 0.5, release = 1.0, pos = 0,
			gate = 1, cutoff = 22e3, bass = 0.0, basefreq=440, freq
			|
			var sig, rate, frames, env, file;
			frames = BufFrames.kr(bufnum);
			rate = freq/basefreq;
			sig = ConvenienceBufferPlay.ar(
				1,
				bufnum,
				rate*BufRateScale.kr(bufnum),
				1,
				pos*frames,
				loop: loop
			);
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate);
			FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
			sig = LPF.ar(sig, cutoff);
			sig = sig + (LPF.ar(sig, 100, bass));
			sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
			sig = LeakDC.ar(sig);
			Out.ar(out, (sig*env));
		}).add;

		SynthDef(\ConvenienceStereoScale, {
			|
			bufnum, out = 0, loop = 0, spread = 1, pan = 0, amp = 0.5,
			attack = 0.01, decay = 0.5, sustain = 0.5, release = 1.0, pos = 0,
			gate = 1, cutoff = 22e3, bass = 0.0, basefreq=440, freq
			|
			var sig, rate, frames, env, file;
			frames = BufFrames.kr(bufnum);
			rate = freq/basefreq;
			sig = ConvenienceBufferPlay.ar(
				2,
				bufnum,
				rate*BufRateScale.kr(bufnum),
				1,
				pos*frames,
				loop: loop
			);
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate);
			FreeSelf.kr(TDelay.kr(Done.kr(env),0.1));
			sig = LPF.ar(sig, cutoff);
			sig = sig + (LPF.ar(sig, 100, bass));
			sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
			sig = LeakDC.ar(sig);
			Out.ar(out, (sig*env));
		}).add;

		/*	  --------------------------------------  */
		/*	  bfft filter bins				synth	 */
		/*	  ------------------------------------- */
		~frame = 1024;
		SynthDef(\ConvenienceBufBins, { | bufnum, out = 0, win = 1, loop = 0, spread = 1, pan = 0, amp = 0.5,
			binRange =#[0, 512], gate = 1, attack = 0.01, decay = 0.01, sustain = 2,
			release = 0.01, pos = 0, rate = 1 |
			var in, chain, env, frames, sig;
			frames = BufFrames.kr(bufnum);
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate, doneAction: 2);
			in = PlayBuf.ar(1, bufnum, rate * BufRateScale.kr(bufnum), startPos: pos * frames, loop: loop);
			chain = FFT(LocalBuf(~frame), in);
			chain = chain.pvcollect(~frame, {| mag, phase, index |
				if(index >= binRange[0], if(index <= binRange[1], mag, 0), 0);
			}, frombin: 0, tobin: (~frame / 2) - 1, zeroothers: 0);
			sig = IFFT(chain, win) * amp * env;
			sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
			sig = LeakDC.ar(sig);
			Out.ar(out, sig);
		}).add;

		SynthDef(\ConvenienceBufBinsScale, { | bufnum, out = 0, win = 1, loop = 0, spread = 1, pan = 0, amp = 0.5,
			binRange =#[0, 512], gate = 1, attack = 0.01, decay = 0.01, sustain = 2,
			release = 0.01, pos = 0, basefreq=440, freq |
			var in, chain, env, frames, rate, sig;
			frames = BufFrames.kr(bufnum);
			rate = freq/basefreq;
			env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate, doneAction: 2);
			in = PlayBuf.ar(1, bufnum, rate * BufRateScale.kr(bufnum), startPos: pos * frames, loop: 0);
			chain = FFT(LocalBuf(~frame), in);
			chain = chain.pvcollect(~frame, {| mag, phase, index |
				if(index >= binRange[0], if(index <= binRange[1], mag, 0), 0);
			}, frombin: 0, tobin: (~frame / 2) - 1, zeroothers: 0);
			sig = IFFT(chain, win) * amp * env;
			sig = Splay.ar(sig, spread: spread, center: pan, level: amp);
			sig = LeakDC.ar(sig);
			Out.ar(out, sig);
		}).add;

		/*	  --------------------------------------  */
		/*	  bfft filter bins input		synth	 */
		/*	  ------------------------------------- */
		/*SynthDef(\ConvenienceInBins, { | out = 0, in = 0, win = 1, amp = 0.5,
		binRange =#[0, 512], gate = 1, attack = 0.01, decay = 0.01, sustain = 2,
		release = 0.01, rate = 1 |
		var sig, chain, env;
		env = EnvGen.ar(Env.adsr(attack, decay, sustain, release), gate, doneAction: 2);
		sig = SoundIn.ar(in);
		chain = FFT(LocalBuf(~frame), sig);
		chain = chain.pvcollect(~frame, {| mag, phase, index |
		if(index >= binRange[0], if(index <= binRange[1], mag, 0), 0);
		}, frombin: 0, tobin: (~frame / 2) - 1, zeroothers: 0);
		Out.ar(out, IFFT(chain, win) * amp * env);
		}).add;*/

		synthsBuild = true;
		"Convenience synths build".postln;
	}

	*prAddEventType {
		Event.addEventType(\Convenience, {
			var numChannels, scaling, fft;

			if (~buf.isNil) {
				var folder = ~folder;
				if (folder.isNil.not) {
					var index = ~index ? 0;
					~buf = Convenience.get(folder, index)
				} {
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

			case
			{fft == 1 and: scaling == 0} {
				~instrument = \ConvenienceBufBins;
				//"FFT".postln;
			}
			{fft == 1 and: scaling == 1} {
				~instrument = \ConvenienceBufBinsScale;
				//"FFT+SCALING".postln;
			}
			{scaling == 1 and: fft == 0} {
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


ConvenienceBufferPlay {
	// copy of PlayBufCF
	*ar { arg numChannels, bufnum=0, rate=1.0, trigger=1.0, startPos=0.0, loop = 0.0,
		lag = 0.1, n = 2; // alternative for safemode

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
				trigger = TDuty.ar( trigger ); // audio rate precision for demand ugens
				index = Stepper.ar( trigger, 0, 0, n-1 );
			},
			{ ^PlayBuf.ar( numChannels, bufnum, rate, trigger, startPos, loop ); } // bypass
		);

		on = n.collect({ |i|
			//on = (index >= i) * (index <= i); // more optimized way?
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
					Gate.kr( rate, on ); // hold rate at crossfade
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
}