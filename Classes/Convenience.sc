Convenience {
	// user config
	classvar loadSynths = true; // should crawler auto load synths
	classvar verbosePosts = false; // debug or interest
	classvar suggestions = false; // you want some?
	classvar <>numFxChannels = 2;

	// private config
	classvar <dir, <buffers, <folderPaths, <patterns;
	classvar tmpName;
	classvar loadFn;
	classvar listWindow;

	classvar working = false;

	const <supportedExtensions = #[\wav, \wave, \aif, \aiff, \flac];

	*initClass { | server |
		server = server ? Server.default;
		buffers = Dictionary.new;
		folderPaths = Dictionary.new;
		patterns = IdentitySet.new;
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

	*properties {
		^[
			'name' -> \nil,
			'numChannels' -> 2,
			'spatProxy' -> \nil,
			'type' -> \Convenience,
			'folder' -> \nil,
			'index' -> 1,
			'dur' -> 4,
			'stretch' -> 1.0,
			'pos' -> 0,
			'loop' -> 0,
			'rate' -> 1,
			'degree' -> 0,
			'octave' -> 4,
			'root' -> 0,
			'scale' -> \nil,
			'cutoff' -> 22e3,
			'res'-> 0.01,
			'fgain' -> 1.0,
			'ftype' -> 0,
			'bass' -> 0,
			'pan' -> 0,
			'width' -> 2.0,
			'spread' -> 0.5,
			'amp' -> 0.5,
			'attack' -> 0.01,
			'sustain' -> 1.0,
			'release' -> 0.5,
			'tempo' -> \nil,
			'tuningOnOff' -> 1,
			'basefreq' -> 440,
			'pitchShiftOnOff' -> 0,
			'pitchRatio' -> 1.0,
			'formantRatio' -> 1.0
		]
	}

	*p { | name, spatProxy, numChannels = 2, type=\Convenience, folder, index = 1, dur = 4, stretch = 1.0,
		pos = 0, loop = 0, rate = 1, degree = 0, octave = 3, root = 0, scale,
		cutoff = 22e3, res = 0.01, fgain = 1.0, ftype = 0, bass = 0, pan = 0,
		width = 2.0, spread = 0.5, amp = 0.5, attack = 0.01,
		sustain=1.0, release = 0.5, tempo, tuningOnOff = 0,
		basefreq = 440, pitchShiftOnOff = 0, pitchRatio = 1.0, formantRatio = 1.0 |

		var properties, pdefnProperties = List.new;

		properties = Dictionary.with(*this.properties.collect{arg item; item});

		if (ConvenientCatalog.synthsBuild, {
			if(name.isNil,{"needs a key aka name, please".throw; ^nil});

			// if folder is unspecified in Convenience.p func
			// for some reason .p does not work with eventType and going to .get
			// if this is not here
			if (folder.isNil, {
				if(Convenience.folders.asArray[0].isNil.not, {
					folder = Convenience.folders.asArray[0];
				}, {Error("Convenience:: no buffers available").throw; ^nil})
			});

			// note note note note
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


		// add to Convenience pattern set reference
		patterns.add(name.asSymbol);
		tmpName = name.asSymbol; // temporary name holder

		// if scale is not set choose classic chromatic
		if(scale.isNil, {
			scale = Scale.chromatic;
		});

		// muligvis noget isNil.not stuff må til her, i et forsøg på value centralisering...
		properties.keysValuesChange { | key, value |
			switch(key)
			{\name}{name}
			{\spatProxy}{spatProxy}
			{\numChannels}{numChannels}
			{\type}{type}
			{\folder}{folder}
			{\index} {index}
			{\dur} {dur}
			{\stretch} {stretch}
			{\pos} {pos}
			{\loop} {loop}
			{\rate} {rate}
			{\degree} {degree}
			{\octave} {octave}
			{\root} {root}
			{\scale} {scale}
			{\cutoff} {cutoff}
			{\res} {res}
			{\fgain} {fgain}
			{\ftype} {ftype}
			{\bass} {bass}
			{\pan} {pan}
			{\width} {width}
			{\amp} {amp}
			{\attack} {attack}
			{\sustain} {sustain}
			{\release} {release}
			{\tempo} {tempo}
			{\tuningOnOff} {tuningOnOff}
			{\basefreq} {basefreq}
			{\pitchShiftOnOff} {pitchShiftOnOff}
			{\pitchRatio} {pitchRatio}
		};

		properties.keysDo{ | key |
			if (((key == \name) or: (key == \tempo) or: (key == \spatProxy)).not, {
				pdefnProperties.add(key)
			})
		};

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

			pdefnProperties = pdefnProperties.collect{ | key |
				var pdefn;
				//switch(key)
				//{\type} {[key.asSymbol, type]}
				//{\scale} {[key.asSymbol, scale]}
				//{
					pdefn = Pdefn((name.asString++"_"++key.asString).asSymbol, properties.at(key)).pattern;
					[key.asSymbol, pdefn]
				//}
			};

			Pdef(name,
				Pbind().patternpairs_(pdefnProperties.collect{ | pair |
					pair;
				}.flat)
			)/* .play(tempo) */;

			if(numChannels.isNil, {
				numChannels = 2;
			});

			if ((Ndef(name).isPlaying).not, {
				Ndef(name).source = Pdef(name);
				Ndef(name).reshaping = \elastic;
				Ndef(name).mold(numChannels);
				Ndef(name).playSpat(spatProxyPath: spatProxy.asSymbol);
			}, {
				if((numChannels == Ndef(name).numChannels).not, {
					Ndef(name).mold(numChannels);
				});
				/* //hmm how to update - > get index from playSpat
				// hmm how to update spatProxyPath, how to get out/bus index from spatProxy=?
				//(bus == Ndef(name).bus.index).not
				if((spatProxy.asSymbol == Ndef(name).spatProxy.asSymbol).not, {
					Ndef(name).mold(numChannels);
					Ndef(name).playSpat(spatProxyPath: spatProxy.asSymbol);
				}); */
			});

		}, {
			"Convenience:: synths not added".postln;
		});
	}

	*s { | name, fadeTime = 1 ...args |
		if (name.isNil.not and: patterns.includes(name.asSymbol), {
			Ndef(name).source.clear; // aka Pdef(name).stop
			Ndef(name).stop(fadeTime);
			patterns.remove(name.asSymbol);
		}, {
			"Convenience:: .s not a running pattern".postln
		})

	}

	*ss { | name, spatProxy ...args |
		Ndef(name).stopSpat(spatProxy.asSymbol);
	}

	*sall { | fadeTime = 1 |
		patterns.do{arg name;
			Ndef(name.asSymbol).source.clear;
			Ndef(name.asSymbol).stop(fadeTime);
			patterns.remove(name.asSymbol);
		}
	}

	*tempo { | name, from, to, secs = 0 |

	}

	*fxs {
		if (ConvenientCatalog.fxsynthsBuild.not, {
			ConvenientCatalog.addFxs(numFxChannels);
		});
		^ConvenientCatalog.fxmodules;
	}

	*fxargs { | fxname |
		if (ConvenientCatalog.fxsynthsBuild.not, {
			ConvenientCatalog.addFxs(numFxChannels);
		});
		^ConvenientCatalog.getFx(fxname.asSymbol).argNames.reject(_ == 'in');
	}

	*pfx { | name, fxs ...args |
		var fxList, chainSize;

		//server = server ? Server.default;

		fxList = Array.with(*fxs);
		chainSize = fxList.size;


		//"fxs: %".format(fxList).postln;
		//args.do{arg i; i.asSymbol.postln};

		if ((chainSize > 0) and: name.isNil.not, {

			if (ConvenientCatalog.fxsynthsBuild.not, {
			ConvenientCatalog.addFxs(numFxChannels);
			});

			// if (Ndef(name).isPlaying.not, {
			// 	Ndef(name).source = Pdef(name);
			// 	Ndef(name).reshaping = \elastic;
			// 	Ndef(name).play
			// });



			/* if (Ndef(name).isPlaying.not, {
				Ndef(name).source = Pdef(name);
				Ndef(name).playN(out)
			}); */

			//"chainSize: %".format(chainSize).postln;

			chainSize.do{ | i |
				///// not working value.calss,.  how to get fx key??? hmm..
				//if ((Ndef((name.asString).asSymbol)[i+1].value.class == fxs[i]).not, {
					//fxList[i].postln;


					if (ConvenientCatalog.fxlist.includesKey(fxList[i].asSymbol), {
						// important to iterate from 1
						// first fx should not be Ndef(\name)[0]
						Ndef(name)[i+1] = \filter -> ConvenientCatalog.getFx(fxList[i].asSymbol);
					}, {
						"Convenience:: fx: % does not exist".format(fxList[i]).postln
					})
				//})
			};

			// default add dc filter
			Ndef(name)[chainSize+1] = \filter -> ConvenientCatalog.getFx(\dc);

			// default add limiter
			Ndef(name)[chainSize+2] = \filter -> ConvenientCatalog.getFx(\limiter);

			// last chain entry is control
			Ndef(name)[chainSize+3] = \set -> Pbind(
				\dur, Pdefn((name++"_dur").asSymbol),
				*args
			);

			//^Ndef(name);
		}, {
			if (chainSize > 0, {

			}, {
				"Convenience:: pfx needs an array of fx keys".postln;

			});
			"Convenience:: pfx needs a name and an array of fx keys".postln;
		})
	}

	*pp { | pattrname, parameter, value |
        if ((pattrname.isNil and: parameter.isNil).not, {
			if (Pdef.all.includesKey(pattrname.asSymbol), {
				if (this.properties.asDict.includesKey(parameter.asSymbol), {
					if (value.isNil.not, {
						// fadeTime hack begin
						var properties, pdefnProperties = List.new;

						properties = Dictionary.with(*this.properties.collect{arg item; item});

						Pdefn((pattrname.asString++"_"++parameter).asSymbol, value);

						properties.keysDo{ | key |
							if (((key == \name) or: (key == \tempo)).not, {
								pdefnProperties.add(key)
							})
						};

						pdefnProperties = pdefnProperties.collect{ | key |
							var pdefn;
							pdefn = Pdefn((pattrname.asString++"_"++key.asString).asSymbol).pattern;
							[key.asSymbol, pdefn]
						};

						Pdef(pattrname,
							Pbind().patternpairs_(pdefnProperties.collect{ | pair |
								pair;
							}.flat)
						);
						
						Pbindef(pattrname, parameter.asSymbol, Pdefn((pattrname.asString++"_"++parameter).asSymbol).pattern);
						// fadeTime hack end
						/* Pbindef only has Pdef's fadeTime when it incrementally 
						changed from a Pdef the first time.
						also Pdefn does not do fadeTime.. so here just used as a look up system */
					}, {
						^Pdefn((pattrname.asString++"_"++parameter).asSymbol).pattern
					})
				}, {
					"Convenience:: pp -> param does not exist".postln
				})
			}, {
				"Convenience:: pp -> pattrname does not exist".postln
			})
        }, {
			"Convenience:: pp -> needs a pattrname and param".postln
		})
    }
	
	*fade { | name, fadeTime |
		//if (patterns.includes(name.asSymbol), {
			//Ndef(name.asSymbol).source.fadeTime_(fadeTime); // ndef soruce -> pdef
			Pdef(name.asSymbol).fadeTime_(fadeTime);
			Ndef(name.asSymbol).fadeTime_(fadeTime); // ndef
		//}, {
		//	"Convenience:: pattern not running".postln
		//})
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
			this.prParseFolders(initpath, depth, server)
		});

		// crawler load synths user config
		if (loadSynths == true, {
			this.addSynths(server);
			if (ConvenientCatalog.fxsynthsBuild.not, {
				ConvenientCatalog.addFxs(numFxChannels);
			});
		});

		

	}

	*prParseFolders{ | initpath, depth = 0, server |
		//var initPathDepthCount = PathName(initpath).fullPath.withTrailingSlash.split(thisProcess.platform.pathSeparator).size;
		var initPathDepth, anythingFound;

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

			/*
			depth control -> here using 'initPathDepthCount-1' because 0 initiator counting seems more logical
			ie. the depth of the init path is 0
			0 means go into the folder specified and check files (aka no depth).
			depth 1 means both the init folder and the folder in that. and so on..
			*/
			if(item.fullPath.split(thisProcess.platform.pathSeparator).size-initPathDepth-1<=depth, {
				loadFolderFlag = true;
				anythingFound = true;
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

		if (anythingFound.asBoolean.not, {"\n\tno folders is staged to load\n".postln});

		// stage work for boot up
		ServerBoot.add(loadFn, server);
		// if server is running create rightaway
		if (server.serverRunning) {
			{
				this.prPipeFoldersToLoadFunc(server)
			}.fork(AppClock)
		};
	}

	*prPipeFoldersToLoadFunc{ | server |
		var news = false;

		folderPaths.keysValuesDo{ | key |
			if (buffers.includesKey(key).not, {news = true});
		};

		/*folderPaths.keysDo{|item|
		"\n\n\t***prPipeFoldersToLoadFunc**\nfolderPath: %\n".format(item).postln
		};*/

		if (folderPaths.isEmpty.not,{

			if (news,{
				folderPaths.keysValuesDo{ | key, path |
					if (buffers.includesKey(key).not,{
						"\nConvenience::crawl -> piping % and loading buffers".format(key).postln;
						this.load(path.asString, server);
						"Convenience::crawl -> done piping and loading %".format(key).postln;

					}, {
						if (verbosePosts, {"Convenience::crawl -> nothing new to %".format(key).postln})
					})
				};

			}, {
				"Convenience::crawl -> new folders not found".postln;
			});


			// update folderPaths to be even with loaded buffers
			folderPaths.keysDo{ | key |
				//key.postln;
				// clean up folderPath dir after loading
				if(buffers.includesKey(key).not,{
					folderPaths.removeAt(key);
					//"removed % from folderPaths".format(key).postln;
				}
			)};

		}, {"crawler did not find any wav/aif/flac files".postln;});
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
				// then
				if(hiddenFile.asBoolean.not, {
					result = supportedExtensions.includes(file.extension.toLower.asSymbol);
				}, {result = false});

				result;
			}; // this func is possibly really prParseFolders' responsibility

			//"files: %".format(files).postln;

			loadedBuffers = files.collect { | file |
				var numChannels;
				working = true;

				//server.sync;
				// use this to get numChannels of file "before" it read into buffer
				//numChannels = SoundFile.use(file.fullPath, {arg qfile; qfile.numChannels});
				server.sync;

				if(verbosePosts.asBoolean,{
					"reading % channel from\n\t %".format(numChannels, file.fileName).postln;
				});

				Buffer.readChannel(server, file.fullPath;, channels: [0]).normalize(0.99);
				/*
				Buffer.readChannel(server, file.fullPath,
					channels: [numChannels.collect{arg i; i}].flat
					//channels: [numChannels.collect{arg i; i}].flat.select{} // only select the chans we want
				).normalize(0.99);
				*/
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

	*prAddEventType {
		Event.addEventType(\Convenience, {
			var bufferNumChannels, outputNumChannels, scaling, pitchshift;

			// if buffer is not directly used
			// use folder and index references
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

			bufferNumChannels = ~buffer.numChannels;
			//"eventType found % bufferNumChannels".format(bufferNumChannels).postln;

			outputNumChannels = ~numChannels;
			//outputNumChannels.postln;

			scaling = ~tuningOnOff;
			if(scaling.isNil) {scaling = 0};
			pitchshift = ~pitchShiftOnOff;
			if(pitchshift.isNil) {pitchshift = 0};

			case
			// favoring pitchshift
			{pitchshift == 1 and: scaling == 0} {
				~instrument = ("ConveniencePitchShift_"++outputNumChannels).asSymbol;
				//"PITCHSHIFT".postln;
			}
			{pitchshift == 1 and: scaling == 1} {
				~instrument = ("ConveniencePitchShiftScale_"++outputNumChannels).asSymbol;
				//"PITCHSHIFT+SCALING".postln;
			}
			{scaling == 1 and: pitchshift == 0} {
				//"SCALING -- ".post;
				switch(bufferNumChannels,
					1, {
						~instrument = ("ConvenienceMonoScale_"++outputNumChannels).asSymbol;
						//"mono".postln;
					},
					2, {
						~instrument = ("ConvenienceStereoScale_"++outputNumChannels).asSymbol;
						//"stereo".postln;
					},
					{
						~instrument = ("ConvenienceMonoScale_"++outputNumChannels).asSymbol;
						//"mono-default".postln;
					}
				);

			}
			{
				//"NORMALES -- ".post;
				switch(bufferNumChannels,
					1, {
						~instrument = ("ConvenienceMono_"++outputNumChannels).asSymbol;
						//"mono".postln;
					},
					2, {
						~instrument = ("ConvenienceStereo_"++outputNumChannels).asSymbol;
						//"stereo".postln;
					},
					{
						~instrument = ("ConvenienceMono_"++outputNumChannels).asSymbol;
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



