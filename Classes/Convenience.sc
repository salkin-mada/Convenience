Convenience {
	// user config
	classvar loadSynths = true; // should crawler auto load synths
	classvar verbosePosts = false; // debug or interest
	classvar suggestions = true; // you want some?

	// private config
	classvar <dir, <buffers, <folderPaths;
	classvar loadFn;
	classvar listWindow;

	classvar working = false;

	const <supportedExtensions = #[\wav, \wave, \aif, \aiff, \flac];

	*initClass { | server |
		server = server ? Server.default;
		buffers = Dictionary.new;
		folderPaths = Dictionary.new;
		loadFn = #{ | server | Convenience.prPipeFoldersToLoadFunc(server) };
		this.prAddEventType;
		"\nConvenience is possible".postln;
		if (suggestions, {
			server.doWhenBooted({
				this.suggestions
			})
		});
	}

	*suggestions {
		if (Main.packages.asDict.includesKey(\Else).not, {
			"\n\tConvenience:: suggests to install the 'Else' Quark".postln;
		});
		if (Main.packages.asDict.includesKey(\Utopia).not, {
			"\n\tConvenience:: suggests to install 'Utopia' Quark (see: BeaconClock)".postln;
		});
		"\n\tConvenience:: welcome".postln;
	}

	*p { | name, type=\Convenience, out = 0, folder, index = 1, dur = 8, stretch = 1.0,
		pos = 0, loop = 0, rate = 1, degree = 0, octave = 3, root = 0, scale,
		cutoff = 22e3, bass = 0, pan = 0, spread = 0, amp = 0.5, attack = 0.1,
		sustain=1.0, release = 0.5, tempo, tuningOnOff = 0,
		basefreq = 440, fftOnOff = 0, binRange = 20, pitchShiftOnOff = 0, pitchRatio = 1.0, formantRatio = 1.0 |

		//var return;


		if (ConvenientCatalog.synthsBuild, {
			if(name.isNil,{"needs a key aka name, please".throw; ^nil});

			// if folder is unspecified in Convenience.p func
			// for some reason .p does not work with eventType and going to .get
			// if this is not here
			if (folder.isNil, {
				if(Convenience.folders.asArray[0].isNil.not, {
					folder = Convenience.folders.asArray[0];
					//"choosing first bufferGroup".postln;
				}, {Error("Conveience:: no buffers available").throw; ^nil})
			});
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
				if (tempo.class == BeaconClock, {
					// great do nothing
					//"\ttempo is BeaconClock controlled".postln
				}, {
					tempo = TempoClock(tempo);
					//"\tusing tempoclock".postln;
				})
			}, {
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
					\rate, rate,
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
		Pdef(name).stop;
	}

	*crawl { | initpath, depth = 0, server |

		// if server is nil set to default
		server = server ? Server.default;

		// if no path is specified open crawl drag'n drop window
		if (initpath.isNil, {
			ConvenientCrawlerView.open(depth, server);
		}, {
			// NO WINDOW USAGE
			// initpath was set when crawl method was called
			// going directly to parsing!
			"Convenience:: crawling".postln;
			this.prParseFolders(initpath, depth, server)
		});

		// crawler load synths user config
		if (loadSynths == true, {
			this.addSynths(server);
		});

	}

	*prParseFolders{ | initpath, depth = 0, server |
		//var initPathDepthCount = PathName(initpath).fullPath.withTrailingSlash.split(thisProcess.platform.pathSeparator).size;
		var initPathDepth;
		
		/*protection against setting an initpath ending with / or // etc,
		which will break the depth control*/
		while({initpath.endsWith("/")}, {
			if(verbosePosts.asBoolean, {"removed /".postln; });
			initpath = initpath[..initpath.size-2]
		});

		initPathDepth = PathName(initpath).fullPath.split($/).size;
	
		server = server ? Server.default;
		dir = initpath; //update getter

		if (verbosePosts, {
			"initDepthCount -> \n\t %".format(initPathDepth).postln;
			"\n__prParseFolders__".post;
			"\n\tinitpath: %".format(initpath).post;
			"\n\tdepth: %\n".format(depth).postln;
		});

		PathName(initpath).deepFiles.do{ | item |
			var loadFolderFlag;

			if (verbosePosts, {
				"item -> \n\t % \n\t depth -> \n\t\t %".format(item.fullPath, item.fullPath.split(thisProcess.platform.pathSeparator).size).postln;
			});

			// depth control -> here using 'initPathDepthCount-1' because 0 initiator counting seems more logical
			//  ie. the depth of the init path is 0
			// 0 means go into the folder specified and check files (aka no depth). depth 1 means both the init folder and the folder in that. and so on..
			if(item.fullPath.split(thisProcess.platform.pathSeparator).size-initPathDepth-1<=depth, {
				loadFolderFlag = true;
			}, {
				loadFolderFlag = false;
			});

			if (loadFolderFlag == true, {
				var folderKey;
				folderKey = this.prKeyify(item.folderName);
				// add to folderPaths if not already present
				if (folderPaths.includesKey(folderKey).not, {
					folderPaths.add(folderKey -> item.pathOnly.asSymbol);
					if(verbosePosts, {"added % to folderPaths".format(folderKey).postln});
				}, {
					// folder already added to folderPaths
					if (verbosePosts, {"folder % included in folderPaths will not be added again".format(item.pathOnly.asSymbol).postln});
				});
			});
		};

		if (verbosePosts, {folderPaths.keysDo{ | item |"\n\t__prParseFolders__folderPath: %\n".format(item).postln}});

		if (folderPaths.isEmpty, {"\n\tno folders is staged to load\n".postln});

		// stage work for boot up
		ServerBoot.add(loadFn, server);
		// if server is running create rightaway
		if (server.serverRunning) {
			{ // do in routine, for s.sync in *load
				// the routine should be in *load though.
				this.prPipeFoldersToLoadFunc(server)
			}.fork(AppClock)
		};
	}

	*prPipeFoldersToLoadFunc{ | server |

		/*folderPaths.keysDo{|item|
		"\n\n\t***prPipeFoldersToLoadFunc**\nfolderPath: %\n".format(item).postln
		};*/

		if (folderPaths.isEmpty.not,{
			"crawl::: piping directory and loading buffers".postln;
			folderPaths.keysValuesDo{ | key, path |
				this.load(path.asString, server);
			};
			"crawl::: done piping and loading".postln;
			// update folderPaths to be even with loaded buffers
			folderPaths.keysDo{ | key |
				//key.postln;
				// clean up folderPath dir after loading
				if(buffers.includesKey(key).not,{
					folderPaths.removeAt(key);
					//"removed % from folderPaths".format(key).postln;
				}
			)};

		}, {"crawler did not find any wav/aiff files".postln;});
	}

	*load { | path, server |
		var folder = PathName(path);
		var files, loadedBuffers, folderKey;
		folderKey = this.prKeyify(folder.folderName);

		server = server ? Server.default;


		//{ // routine -> load files into buffers
		// check that folderKey does not already exist
		if (buffers.includesKey(folderKey).not, {

			// check header only return item if it is supported
			// files = folder.entries.select { | file |
			// 	supportedExtensions.includes(file.extension.toLower.asSymbol)
			// };

			files = folder.entries.select { | file |
				var hiddenFile;
				var result;
				// check if file is dot type
				file.fileName.do{ | char, i |
					if(char.isPunct and: i == 0, {
						if(verbosePosts == true, {
							"found a dot file - avoiding -> %".format(file).postln
						});
						hiddenFile = true;
					})
				};

				if(hiddenFile.asBoolean.not, {
					result = supportedExtensions.includes(file.extension.toLower.asSymbol);
				}, {result = false});

				result;
			}; // this func is possibly really prParseFolders' responsibility

			//"files: %".format(files).postln;

			loadedBuffers = files.collect { | file |
				server.sync;
				working = true;
				if(verbosePosts.asBoolean,{
					"reading first channel from\n\t %".format(file.fileName).postln;
				});
				Buffer.readChannel(server, file.fullPath;, channels: [0]).normalize(0.99);
			};

			if (working == true, {
				working = false;
			});

			//"\n\t loadedBuffers from folder: % --> %".format(folder.folderName,loadedBuffers).postln;
			//server.sync; // danger danger only working when used before boot use update here instead?
			//"loading into memory, hang on".postln;

			// add loadedBuffers to dictionary with key from common folder
			if (loadedBuffers.isEmpty.not, {
				if (buffers.includesKey(folderKey).not, {
					if(verbosePosts.asBoolean,{
						"added new folder as key: %".format(folderKey).postln;
					});
					//  add and remove spaces in folder name
					buffers.add(folderKey -> loadedBuffers);
					ConvenientListView.update
				})
			}, {
				//"no soundfiles in : %, skipped".format(folder.folderName).postln;
				// update folderPaths
				// folderPaths.removeAt(folderKey);
			});
		}, {
			if(verbosePosts.asBoolean,{
				"folder % already loaded".format(folderKey).postln
			});
		});
		//}.fork{AppClock};
	}

	*free { | folder, server |

		if (folder.isNil,{ // free all no folder specified
			if (buffers.isEmpty.not, {
				this.prFreeBuffers;
				this.prClearFolderPaths; // also reset parser -> empty dictionary of folder paths
				server = server ? Server.default;
				ServerBoot.remove(loadFn, server);
				"all buffers freed".postln;
				// update the list view
				ConvenientListView.update
			}, {
				"no buffers to free".postln
			})
		}, { // free only specified folder
			if (buffers.includesKey(folder), {
				buffers.at(folder).do{ | buffer |
					if (buffer.isNil.not, {
						//"freeing buffer: %".format(buffer).postln;
						buffer.free;
					})
				};
				// buffers.keysValuesDo { | item |
				// 	if (item == folder, {
				// 		"freeing folder: %".format(item).postln;
				// 		item.do { | buffer |
				// 			if (buffer.isNil.not, {
				// 				"freeing buffer: %".format(buffer).postln;
				// 				buffer.free;
				// 			})
				// 		}
				// 	});
				// };
				buffers.removeAt(folder);
				"folder % is freed".format(folder).postln;
				ConvenientListView.update
			});

			// removing the absolute path to folder
			if (folderPaths.includesKey(folder), {
				folderPaths.removeAt(folder);
				//"real folder path removed".postln;
			})
		});
	}

	// *clearFolderPathsDict {
	// 	this.prClearFolderPaths;
	// }

	*randomFolder {
		^this.folderNum(this.folders.size.rand)
	}

	*prClearFolderPaths {
		folderPaths.clear
	}

	*prFreeBuffers {
		buffers.do { | folders |
			folders.do { | buffer |
				if (buffer.isNil.not) {
					buffer.free
				}
			}
		};
		buffers.clear;
	}

	// *prFreeFolder { | folder |
	// 	buffers.do { | folders |
	// 		folder.do { | buf |
	// 			if (buf.isNil.not) {
	// 				buf.free
	// 			}
	// 		}
	// 	};
	// 	buffers.clear;
	// }

	*prUpdateListView {
		ConvenientListView.update;
	}

	*addSynths { | server |
		if (ConvenientCatalog.synthsBuild.asBoolean.not,{
			ConvenientCatalog.addSynths(server);
		});
	}

	*get { | folder, index |

		if (buffers.notEmpty, {

			var bufferGroup;

			// if folder is unspecified
			if (folder.isNil, {
				//"*get folder is nil".postln;
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
					Error("Convenience::*get::
user is asking for folder which is not there,
and *get cant find another folder to replace it with").throw;
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
			Error("Convenience::*get:: buffers is empty").throw;
			^nil
		});
	}

	*folders {
		^buffers.keys
	}

	*size {
		^this.buffers.values.collect{ | i | i.size}.sum
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
		^buffers.keysValuesDo { | folderName, bufferArray |
			bufferArray.do{ | buffer | "% -> %".format(folderName, buffer).postln
			}
		}
	}

	*list { | gui = false |
		buffers.keysValuesDo { | folderName, buffers |
			"% [%]".format(folderName, buffers.size).postln
		};

		if(working == true, {
			"not all folders have been loaded yet, working on it".postln;
		});

		if (gui == true, {
			listWindow = ConvenientListView.open;
		});
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

	*bufferKeys {
		^buffers.keys.collect { | keys |
			keys;
		};
		// ^buffers.keys.collect { | keys |
		// 	keys;
		// }.asArray;
	}

	*properties {
		^[
			"name", "type", "out", "folder", "index", "dur", "stretch",
			"pos", "loop", "rate", "degree", "octave", "root", "scale",
			"cutoff", "bass", "pan", "spread", "amp", "attack",
			"sustain", "release", "tempo", "tuningOnOff",
			"basefreq", "fftOnOff", "binRange", "pitchShiftOnOff", "pitchRatio", "formantRatio"
		]
	}

	*prAddEventType {
		Event.addEventType(\Convenience, {
			var numChannels, scaling, fft, pitchshift;

			if (~buffer.isNil) {
				var folder = ~folder;
				var index = ~index;
				//if (folder.isNil.not) {
				//var index = ~index ? 0;

				~buffer = Convenience.get(folder, index)
				//} {

				// pair split if folder is nil
				// var sample = ~sample;
				// if (sample.isNil.not) {
				// 	var pair, folder, index;
				// 	pair = sample.split($:);
				// 	folder = pair[0].asSymbol;
				// 	index = if (pair.size == 2) { pair[1].asInt } { 0 };
				// 	~buffer = Convenience.get(folder, index)
				// }
				//}
			};

			numChannels = ~buffer.bufnum.numChannels;

			scaling = ~tuningOnOff;
			if(scaling.isNil) {scaling = 0};
			fft = ~fftOnOff;
			if(fft.isNil) {fft = 0};
			pitchshift = ~pitchShiftOnOff;
			if(pitchshift.isNil) {pitchshift = 0};

			case
			// favoring pitchshift
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
			~bufnum = ~buffer.bufnum;
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

	*modul { | name = \ConvenientSynthGraph, busnum, numLayers = 10, out |
		if(busnum.isNil.not, {
			ConvenientCatalog.prSynthGraph(name, busnum, numLayers, out)
		}, {"no busnum(s) specified".postln})
	}

	*lfos { | numLFOs = 10, freq = 0.1 |
		ConvenientCatalog.numLFOs_(numLFOs);
		ConvenientCatalog.prMakeLFOs(freq);
	}

	*map { | target = \ConvenientSynthGraph, prob = 0.75 |
		ConvenientCatalog.prMapLFOs(target, prob);
	}


}





/*

(
Pdef(\main, {
Ppar([
Pbindef(\first,
\dur, Pseq([Rest(3), 8], inf),
\degree, Pseq([5,9], inf)
).trace(prefix: '1'),
Pbindef(\second,
\dur, Pseq([Rest(8), 12], inf),
\degree, Pseq([2,3], inf),
\octave, 6
).trace(prefix: '2'),
Pbindef(\third,
\dur, Pseq([Rest(15), 20], inf),
\degree, Pseq([10,8], inf),
\octave, 5
).trace(prefix: '3')
])
}).play
)

(
Pdef(\main, {
Ppar([
Pbindef(\first,
\dur, Pseq([Rest(0.5), 8], inf),
\degree, Pseq([5,9], inf)
).trace(prefix: '1'),
Pbindef(\second,
\dur, Pseq([Rest(1), 12], inf),
\degree, Pseq([2,3], inf),
\octave, 6
).trace(prefix: '2'),
Pbindef(\third,
\dur, Pseq([Rest(3), 20], inf),
\degree, Pseq([10,8], inf),
\octave, 5
).trace(prefix: '3')
])
}).play
)



{SinOsc.ar(730)!2}.play



Pif

PS  classes

Pdef(\main).isPlaying


.poll(,)*/



