+ C {
    *load { | path, type, server |
        var folder, files, loadedBuffers = [], folderKey;
        var file; // single file loading

        server = server ? Server.default;

        if(type.notNil, {
            var condition = Condition.new;
            if(type == "folder") {
                folder = PathName(path);
                folderKey = this.prKeyify(folder.folderName);

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
                                if(verbose == true, {
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
                    if (files.size > 0, {
                        loadedBuffers = files.collect { | file |
                            var numChannels;
                            working = true;

                            // use this to get numChannels of file "before" it read into buffer
                            numChannels = SoundFile.use(file.fullPath, {arg qfile; qfile.numChannels});
                            server.sync;

                            if(verbose.asBoolean,{
                                "file: %\n\tcontains % chans".format(file.fileName, numChannels).postln;
                            });

                            case
                            {numChannels == 1}
                            {
                                if(verbose.asBoolean,{"convenience:: loading 1 channels".postln});
                                Buffer.readChannel(server, file.fullPath, 
                                    channels: [0],
                                    // action: {condition.unhang}
                                ).normalize(0.99);
                            }
                            {numChannels == 2}
                            {
                                if(verbose.asBoolean,{"convenience:: loading 2 channels".postln});
                                Buffer.readChannel(server, file.fullPath,
                                    channels: [numChannels.collect{arg i; i}].flat,
                                    //channels: [numChannels.collect{arg i; i}].flat.select{} // only select the chans we want
                                    // action: {condition.unhang}
                                ).normalize(0.99);
                            }
                            {numChannels > 2}
                            {
                                if(verbose.asBoolean,{"convenience:: loading 1 channels".postln});
                                Buffer.readChannel(server, file.fullPath;,
                                    channels: [0],
                                    // action: {condition.unhang}
                                ).normalize(0.99);
                            }
                            {numChannels == 0}
                            {
                                if(verbose.asBoolean,{"convenience:: does not understand file channel count".warn});
                                // nil
                            };
                            // condition.hang;
                        };

                        // if (working == true, {
                        //     working = false;
                        //     "Convenience::crawl -> done loading %".format(folderKey).postln;
                        // });
                    }, {
                        "Convenience::crawl -> no sound files in %".format(folderKey).postln;
                        //files = nil;
                    });


                    //"\n\t loadedBuffers from folder: % --> %".format(folder.folderName,loadedBuffers).postln;
                    //server.sync; // danger danger only working when used before boot use update here instead?
                    //"loading into memory, hang on".postln;

                    // add loadedBuffers to dictionary with key from common folder
                    if (loadedBuffers.isEmpty.not, {
                        if (buffers.includesKey(folderKey).not, {
                            if(verbose.asBoolean,{
                                "new folder key: %".format(folderKey).postln;
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
                    if(verbose.asBoolean,{
                        "folder % already loaded".format(folderKey).postln
                    });
                });
                //}.fork{AppClock};
                if (working == true, {
                    working = false;
                    "Convenience::crawl -> done loading %".format(folderKey).postln;
                });
            };

            if(type == "file") {
                var numChannels, buffer;
                file = PathName(path);
                working = true;

                if(verbose) {"Convenience:: *load -> it's a file".postln};

                // get numChannels of file "before" its read into buffer
                numChannels = SoundFile.use(file.fullPath, {arg qfile; qfile.numChannels});
                server.sync;

                if(verbose.asBoolean,{
                    "file: %\n\tcontains % chans".format(file.fullPath, numChannels).postln;
                });

                case
                {numChannels == 1}
                {
                    if(verbose.asBoolean,{"convenience:: loading 1 channels".postln});
                    buffer = Buffer.readChannel(server, file.fullPath,
                        channels: [0],
                        // action: {condition.unhang}
                    ).normalize(0.99);
                }
                {numChannels == 2}
                {
                    if(verbose.asBoolean,{"convenience:: loading 2 channels".postln});
                    buffer = Buffer.readChannel(server, file.fullPath,
                        channels: [numChannels.collect{arg i; i}].flat,
                        // //channels: [numChannels.collect{arg i; i}].flat.select{} // only select the chans we want
                        // action: {condition.unhang}
                    ).normalize(0.99);
                }
                {numChannels > 2}
                {
                    if(verbose.asBoolean,{"convenience:: loading 1 channels".postln});
                    buffer = Buffer.readChannel(server, file.fullPath,
                        channels: [0],
                        // action: {condition.unhang}
                    ).normalize(0.99);
                }
                {numChannels == 0}
                {
                    if(verbose.asBoolean,{"C::does not understand file channel count".warn});
                    nil
                };

                // change this to condition!!
                // server.sync;

                // condition.hang;


                if (working == true, {
                    working = false;
                    "C::crawl -> done loading single file to dict key % -> %".format(cFolder, file.fullPath).postln;
                });

                // pretty hacky kode.. serouslylulyluly ugly
                if(buffers[cFolder].isNil.not) {
                    buffers[cFolder].add(buffer);
                } {buffers.add(cFolder -> buffer.asArray);};

                if(verbose == true, {
                    "C::single file load:: buffer: % ".format(buffer).postln;
                });

                ConvenientListView.update;

            };

            if(type == "force") {
                folder = PathName(path);
                folderKey = this.prKeyify(folder.folderName);
                if (buffers.includesKey(folderKey).not) {
                    files = folder.entries.select { | file |
                        var hiddenFile;
                        var result;

                        // check if file is dot type
                        file.fileName.do{ | char, i |
                            if(char.isPunct and: i == 0, {
                                if(verbose == true, {
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
                    if (files.size > 0, {
                        loadedBuffers = files.collect { | file |
                            var numChannels;
                            working = true;

                            // use this to get numChannels of file "before" it read into buffer
                            numChannels = SoundFile.use(file.fullPath, {arg qfile; qfile.numChannels});
                            server.sync;

                            if(verbose.asBoolean,{
                                "file: %\n\tcontains % chans".format(file.fileName, numChannels).postln;
                            });

                            case
                            {numChannels == 1}
                            {
                                if(verbose.asBoolean,{"convenience:: loading 1 channel".postln});
                                Buffer.readChannel(server, file.fullPath, 
                                    channels: [0],
                                    // action: {condition.unhang}
                                ).normalize(0.99);
                            }
                            {numChannels == 2}
                            {
                                if(verbose.asBoolean,{"convenience:: loading 2 channels".postln});
                                Buffer.readChannel(server, file.fullPath,
                                    channels: [numChannels.collect{arg i; i}].flat,
                                    //channels: [numChannels.collect{arg i; i}].flat.select{} // only select the chans we want
                                    // action: {condition.unhang}
                                ).normalize(0.99);
                            }
                            {numChannels > 2}
                            {
                                if(verbose.asBoolean,{"convenience:: numChannels is larger than 2 -> loading 1 channel".postln});
                                Buffer.readChannel(server, file.fullPath;,
                                    channels: [0],
                                    // action: {condition.unhang}
                                ).normalize(0.99);
                            }
                            {numChannels == 0}
                            {
                                if(verbose.asBoolean,{"convenience:: does not understand file channel count".warn});
                                // nil
                            };
                            // condition.hang;
                        };

                        // if (working == true, {
                        //     working = false;
                        //     "Convenience::crawl -> done loading %".format(folderKey).postln;
                        // });
                    }, {
                        "Convenience::crawl -> no sound files in %".format(folderKey).postln;
                        //files = nil;
                    });


                    //"\n\t loadedBuffers from folder: % --> %".format(folder.folderName,loadedBuffers).postln;
                    //server.sync; // danger danger only working when used before boot use update here instead?
                    //"loading into memory, hang on".postln;

                    // add loadedBuffers to dictionary with key from common folder
                    if (loadedBuffers.isEmpty.not, {
                        if (buffers.includesKey(folderKey).not, {
                            if(verbose.asBoolean,{
                                "C::crwal:foce -> new folder key: %".format(folderKey).postln;
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
                } {
                    if(verbose.asBoolean,{
                        "folder % already loaded -> but forcing".format(folderKey).postln
                    });
                    files = folder.entries.select { | file |
                        var hiddenFile;
                        var result = false;

                        // check if file is dot type
                        file.fileName.do{ | char, i |
                            if(char.isPunct and: i == 0, {
                                if(verbose == true, {
                                    "found a dot file - avoiding -> %".format(file).postln
                                });
                                hiddenFile = true;
                            })
                        };
                        // then
                        if(hiddenFile.asBoolean.not, {
                            result = supportedExtensions.includes(file.extension.toLower.asSymbol);
                        }, {result = false});
                        // check that file is not already included from last folder load aka this is force mode, but "intelligent" :)~
                        Buffer.cachedBuffersDo(server, {|buffer| if (buffer.path == file.fullPath) {result = false}});
                        // buffers[folderKey].do{ | buffer, n |
                        // // this.get(folderKey, n).path.postln;
                        // // file.fullPath.postln;
                        //   if (this.get(folderKey, n).path == file.fullPath) {
                        //     result = false;
                        //     "C::crawl:force -> new files in key: %".format(folderKey).postln;
                        //   } {"SAME".postln;};
                        // };
                        result;
                    }; // this func is possibly really prParseFolders' responsibility

                    //"files: %".format(files).postln;
                    if (files.size > 0, {
                        loadedBuffers = files.collect { | file |
                            var numChannels;
                            working = true;

                            // use this to get numChannels of file "before" it read into buffer
                            numChannels = SoundFile.use(file.fullPath, {arg qfile; qfile.numChannels});
                            server.sync;

                            if(verbose.asBoolean,{
                                "file: %\n\tcontains % chans".format(file.fileName, numChannels).postln;
                            });

                            case
                            {numChannels == 1}
                            {
                                if(verbose.asBoolean,{"convenience:: loading 1 channels".postln});
                                Buffer.readChannel(server, file.fullPath, 
                                    channels: [0],
                                    // action: {condition.unhang}
                                ).normalize(0.99);
                            }
                            {numChannels == 2}
                            {
                                if(verbose.asBoolean,{"convenience:: loading 2 channels".postln});
                                Buffer.readChannel(server, file.fullPath,
                                    channels: [numChannels.collect{arg i; i}].flat,
                                    //channels: [numChannels.collect{arg i; i}].flat.select{} // only select the chans we want
                                    // action: {condition.unhang}
                                ).normalize(0.99);
                            }
                            {numChannels > 2}
                            {
                                if(verbose.asBoolean,{"convenience:: loading 1 channels".postln});
                                Buffer.readChannel(server, file.fullPath;,
                                    channels: [0],
                                    // action: {condition.unhang}
                                ).normalize(0.99);
                            }
                            {numChannels == 0}
                            {
                                if(verbose.asBoolean,{"convenience:: does not understand file channel count".warn});
                                // nil
                            };
                            // condition.hang;
                        };

                        // if (working == true, {
                        //     working = false;
                        //     "Convenience::crawl -> done loading %".format(folderKey).postln;
                        // });
                    }, {
                        "Convenience::crawl -> no sound files in %".format(folderKey).postln;
                        //files = nil;
                    });


                    //"\n\t loadedBuffers from folder: % --> %".format(folder.folderName,loadedBuffers).postln;
                    //server.sync; // danger danger only working when used before boot use update here instead?
                    //"loading into memory, hang on".postln;

                    // add loadedBuffers to dictionary with key from common folder
                    if (loadedBuffers.isEmpty.not) {
                        if(verbose.asBoolean,{
                            "C::crawl:force -> adding new files to folder key: %".format(folderKey).postln;
                        });
                        buffers.keysValuesDo{ | key, value |
                            var newValue;
                            if (key == folderKey) {
                                loadedBuffers.do{ | buffer |
                                    value=value.asList.add(buffer)
                                };
                                buffers.add(key -> value);
                            }
                        };
                        ConvenientListView.update
                    }
                };
                //}.fork{AppClock};
                if (working == true, {
                    working = false;
                    "Convenience::crawl -> done loading %".format(folderKey).postln;
                });
            };
            // });
        }, {"Convenience:: no type to loadfunc".postln});

    }

    *free { | folder, server |

        if (folder.isNil,{ // free all no folder specified
            if (buffers.isEmpty.not, {
                this.prFreeBuffers;
                this.prClearFolderPaths; // also reset parser -> empty dictionary of folder paths
                server = server ? Server.default;
                ServerBoot.remove(Convenience.loadFn, server);
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
            }, {
                "there is no folder to free".postln;
            });

            // removing the absolute path to folder
            if (folderPaths.includesKey(folder), {
                folderPaths.removeAt(folder);
                //"real folder path removed".postln;
            })
        });
    }

    *crawl { | initpath, depth = 0, force = false, disableRegex = true, server |
        // if server is nil set to default
        server = server ? Server.default;

        // if no path is specified open crawl drag'n drop window
        if (initpath.isNil, {
            ConvenientCrawlerView.open(depth, force, server);
        }, {
            // NO WINDOW USAGE
            // initpath was set when crawl method was called
            // going directly to parsing!
			if(verbose) {
				initpath.postln;
			};
            //
			/* escape special characters (PathName is using glob)
			https://github.com/supercollider/supercollider/issues/6139 */
			if(disableRegex.asBoolean) {
				if(verbose) {"C:: removing globbin chars if any".postln};
				// initpath = initpath.replace("[", "\\[").replace("]", "\\]").replace("?","\\?")
				initpath = initpath.escape(Convenience.globChars);
			};
            if(PathName(initpath).isFolder, {
                if(verbose){"crawl:: this is a folder".postln};
                this.prParseFolders(initpath, depth, force, server);
            }); 
            if(PathName(initpath).isFile, {
                if(verbose){"crawl:: this is a file".postln};
                this.prFileToLoadFunc(initpath, server);
            }); 

        });

        // crawler load synths user config
        if (loadSynths == true, {
            this.addSynths(server);
            this.addFxSynths(numFxChannels);
        });
    }

    *prParseFolders{ | initpath, depth = 0, force = false, server |
        var initPathDepth, anythingFound;
        //var initPathDepthCount = PathName(initpath).fullPath.withTrailingSlash.split(thisProcess.platform.pathSeparator).size;

        /*protection against setting an initpath ending with / or // etc,
        which will break the depth control*/
        while({initpath.endsWith("/")}, {
            if(verbose.asBoolean, {"C:: removed /".postln; });
            initpath = initpath[..initpath.size-2]
        });

        // clean out platform (OS) separator if it differs from /
		// Windows I am looking at you
        initpath = initpath.tr(Platform.pathSeparator , $/);

		if(verbose) {"C:: initpath post all"+"clean outs".quote+": %".format(initpath).postln};

        initPathDepth = PathName(initpath).fullPath.split($/).size;

        server = server ? Server.default;
        dir = initpath; //update getter

        if (verbose, {
            "initDepthCount -> \n\t %".format(initPathDepth).postln;
            "\n__prParseFolders__".post;
            "\n\tinitpath: %".format(initpath).post;
            "\n\tdepth: %\n".format(depth).postln;
        });
        PathName(initpath).deepFilesEscapedPath.do{ | item |
            var loadFolderFlag;

            if (verbose, {
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
			if(verbose, {"C:: due to path depth of item -> should load item: %".format(loadFolderFlag).postln});

            if (loadFolderFlag == true, {
                var folderKey;
                folderKey = this.prKeyify(item.folderName);
                // add to folderPaths if not already present
                if (folderPaths.includesKey(folderKey).not, {
                    folderPaths.add(folderKey -> item.pathOnly.asSymbol);
                    if(verbose, {"added % to folderPaths".format(folderKey).postln});
                }, {
                    // folder already added to folderPaths
                    if (verbose, {"folder % included in folderPaths will not be added again".format(item.pathOnly.asSymbol).postln});
                });
            });
        };

        if (verbose, {folderPaths.keysDo{ | item |"\n\t__prParseFolders__folderPath: %\n".format(item).postln}});

        if (anythingFound.asBoolean.not, {"\n\tno folders is staged to load\n".postln});

        // if server is running create rightaway
        if (server.serverRunning) {
            {
                this.prPipeFoldersToLoadFunc(force, server)
            }.fork(AppClock)
        } {
            // stage work for boot up
            ServerBoot.add(Convenience.loadFn, server);
        };
    }

    *prPipeFoldersToLoadFunc{ | force, server |
        var news = false;
        force = force ? false;

        folderPaths.keysValuesDo{ | key |
            if (buffers.includesKey(key).not and: (force.asBoolean.not), {news = true});
        };

        /*folderPaths.keysDo{|item|
            "\n\n\t***prPipeFoldersToLoadFunc**\nfolderPath: %\n".format(item).postln
        };*/

        if (folderPaths.isEmpty.not, {
            if (news,{
                folderPaths.keysValuesDo{ | key, path |
                    if (buffers.includesKey(key).not,{
                        //"Convenience::crawl -> piping % to .load".format(key).postln;
                        this.load(path.asString, "folder", server);
                        //"Convenience::crawl -> done piping and loading %".format(key).postln;

                    }, {
                        if (verbose, {"Convenience::crawl -> nothing new to %".format(key).postln})
                    })
                };
                if (verbose, {"Convenience:: pipeFolderToloadFunc:: loading done".postln})

            }, {
                "C::crawl -> no folders that have not already been loaded".postln;
                if (force) {
                    "C::crawl -> but forcing - loading anyway".postln;
                    folderPaths.keysValuesDo{ | key, path |
                        // if (buffers.includesKey(key).not,{
                        this.load(path.asString, "force", server);
                        // }, {
                        // if (verbose, {"Convenience::crawl -> nothing new to %".format(key).postln})
                        // })
                    };
                    if (verbose, {"Convenience:: pipeFolderToloadFunc:: loading done".postln})
                }
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

        }, {"Convenience::crawl -> no folder paths".postln;});
    }

    *prFileToLoadFunc{ | filepath, server | 
        filepath = this.prRemoveWindowsSeparator(filepath);
        server = server ? Server.default;

        if (verbose, {
            "\n\tfile path: %".format(filepath).postln;
        });

        if(supportedExtensions.includes(
            PathName(filepath).extension.toLower.asSymbol
        ).not, {
            "C::format not supported".warn;
            ^nil
        });

        if (server.serverRunning) {
            var news;

            if (buffers[cFolder].isNil.not) {
                news = buffers[cFolder].any{ | buffer | buffer.path == PathName(filepath).fullPath}.not;
            } {
                news = true;
            };

            if (news,{
                {
                    this.load(PathName(filepath).fullPath, "file", server);
                }.fork(AppClock)
            }, {"C::crawl -> file already loaded".postln});

            if (verbose, {"C::prFileToLoadFunc loading done".postln})

        } {
            "Convenience:: Server not running - needed for single file loads".postln

            // TODO(salkin-mada): 
            // stage single file load for boot up
            //
            // folderPaths.add(cFolder -> PathName(filepath).pathOnly.asSymbol);
            // ServerBoot.add(loadFileFn, server);
        };
    }

}

+ String {
	escape {|chars| ^chars.inject(this, _.escapeChar(_))}
}

+ PathName {
	deepFilesEscapedPath {|chars|
		^this.entries.collect({ | item |
			item = PathName(item.fullPath.escape(Convenience.globChars));
			if(item.isFile, {
				item
			},{
				item.deepFilesEscapedPath
			})
		}).flat
	}
}
