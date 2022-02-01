Convenience {
    // user config
    classvar >loadSynths = true; // should crawler auto load synths
    classvar >verbose = false; // debug or interest
    classvar >suggestions = false; // you want some?
    classvar <>numFxChannels = 2; // default number of fx channels
    classvar <>pattern_history_size = 100; // for *repeat

    // private config
    classvar <dir, <buffers, <folderPaths, <filePaths, <patterns, <patterns_history, <inputs;
    classvar tmpName;
    classvar loadFn;
    classvar listWindow;
    // classvar <nodegroup;

    classvar working = false;

    const <supportedExtensions = #[\wav, \wave, \aif, \aiff, \aifc, \flac, \ogg];

    *initClass { | server |
        server = server ? Server.default;
        buffers = Dictionary.new;
        folderPaths = Dictionary.new;
        filePaths = Dictionary.new;
        patterns = IdentitySet.new;
        patterns_history = Dictionary.new;
        inputs = IdentitySet.new;
        loadFn = #{ | server | Convenience.prPipeFoldersToLoadFunc(server: server) };
        this.prAddEventType;
        "\n*** Convenience is possible ***".postln;
        if(Platform.ideName=="scnvim", {
            "SCNvim FTW:".postln;
        });
        if (suggestions, {
            server.doWhenBooted({
                this.suggestions
            })
        });
        // group for Convenience Ndefs
        // server.doWhenBooted{
        // 	nodegroup = Group.new; "adding group for Convenience".postln
        // }
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

    // would be nice to have something for thisProcess.hardstop ( Main )
    //*emptyDicts {
    //	//safety for Ndef group reorder
    //	{
    //		patterns.clear;
    //		inputs.clear;
    //		"Convenience:: dictionaries cleared".postln;
    //	}.doOnCmdPeriod;
    //}

    *properties {
        ^[
            'name' -> \nil,
            'bus' -> 0,
            'numChannels' -> 2,
            'type' -> \Convenience,
            'seed' -> 666,
            'quant' -> 0,
            'folder' -> \nil,
            'index' -> 0,
            'dur' -> 4,
            'stretch' -> 1.0,
            'timingOffset' -> 0.0,
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
            'tuningOnOff' -> 0,
            'basefreq' -> 440,
            'pst' -> 0,
            'pr' -> 1.0,
            'fr' -> 1.0
        ]
    }

    *p { arg name, bus = 0, numChannels= 2, type=\Convenience, seed=666, quant= 0, folder, index= 0, dur= 4,
        timingOffset = 0, stretch= 1.0, pos= 0, loop= 0, rate= 1, degree= 0, octave= 3, root= 0, scale,
        cutoff= 22e3, res= 0.01, fgain= 1.0, ftype= 0, bass = 0, pan= 0, width= 2.0, spread= 0.5, amp= 0.5,
        attack= 0.01, sustain=1.0, release= 0.5, tempo, tuningOnOff= 0, basefreq= 440, pst= 0, pr= 1.0, fr= 1.0,
        replay= 1, server ...args;

        var properties, pdefnProperties = List.new;
        properties = Dictionary.with(*this.properties.collect{arg item; item});

        "extra args: %".format(args).postln; // hallo hallo halooo

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
            // dont allow (change) strings in folder argument
            if (folder.isString) {folder = folder.asSymbol};

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

            server = server ? Server.default;
            if((patterns.includes(name.asSymbol)).not, {
                // check if Ndef already exists by looking up all Ndefs on given server
                // and checking that it is a Ndef (dictFor will return ProxySpace.nil if Ndef.key is not present)
                // therefor check with isKindOf
                if(Ndef.dictFor(server)[name.asSymbol].isKindOf(Ndef), {
                    "some NodeProxy Definition called: % already exists".format(name).warn;
                    ^nil
                }, {
                    // add to Convenience pattern set reference
                    if(verbose) {"new  - welcome".postln};
                    patterns.add(name.asSymbol);
                })
            });
            tmpName = name.asSymbol; // temporary name holder

            // if scale is not set choose classic chromatic
            if(scale.isNil, {
                scale = Scale.chromatic;
            });

            // muligvis noget isNil.not stuff må til her, i et forsøg på value centralisering...
            properties.keysValuesChange { | key, value |
                switch(key)
                {\name}{name}
                {\bus}{bus}
                {\numChannels}{numChannels}
                {\type}{type}
                {\seed}{seed}
                {\quant}{quant}
                {\folder}{folder}
                {\index} {index}
                {\dur} {dur}
                {\timingOffset} {timingOffset}
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
                {\pst} {pst}
                {\pr} {pr}
                {\fr} {fr}
            };

            properties.keysDo{ | key |
                if (((key == \name) or: (key == \seed) or: (key == \quant)/*  or: (key == \tempo) */).not, {
                    pdefnProperties.add(key)
                })
            };
            // also add the ...args
            // args.do{ | arg |
            //     pdefnProperties.add(arg)
            // };

            // decide what clock to use
            // check if Utopia is in Class library
            /* if ( Main.packages.asDict.includesKey(\Utopia) == true, {
                if (tempo.class == BeaconClock, {
                    // great do nothing
                    //"\ttempo is BeaconClock controlled".postln
                }, {
                    tempo = TempoClock(tempo);
                    //"\tusing tempoclock".postln;
                })
            }, {
                tempo = TempoClock(tempo);
            }); */

            pdefnProperties = pdefnProperties.collect{ | key |
                var pdefn;
                // allow empty symbols in folder property
                if (key == 'folder') {
                    // if (folder.asString.isEmpty) {
                    if (key.asString.isEmpty) {
                        key = nil;
                        "C:: folder symbol is empty..".postln;
                    }
                };
                //switch(key)
                //{\type} {[key.asSymbol, type]}
                //{\scale} {[key.asSymbol, scale]}
                //{
                pdefn = Pdefn((name.asString++"_"++key.asString).asSymbol, properties.at(key)).pattern;
                [key.asSymbol, pdefn]
                //}
            };

            if(patterns_history.includesKey(name).not) {
                patterns_history.add(name.asSymbol -> List.new);
                if(verbose) {"adding pattern history list for %".format(name).postln};
            };

            Pdef(name,
                Pseed(seed,
                    Pbind().patternpairs_(pdefnProperties.collect{ | pair |
                        pair;
                    }.flat).collect{|env|
                        if(patterns_history[name].size >= pattern_history_size, {
                            patterns_history[name].removeAt(0);
                        });
                        patterns_history[name].add(env);
                        env;
                    }
                )
            );

            if(numChannels.isNil, {
                numChannels = 2;
            });

            if ((Ndef(name).isPlaying).not, {
                Ndef(name).source = Pdef(name);
                Ndef(name).reshaping = \elastic;
                Pdef(name).quant_(quant);
                Ndef(name).quant_(quant);
                fork{
                    // if((patterns.includes(name.asSymbol)).not, {
                    // reorder Ndef (fix for using Bus.audio as .p(bus) arg, IF Bus was created before this Convenience pattern/ndef)
                    // hacky.. bad programming? some fundamental sc misunderstading here?
                    // "Convenience:: reorder - group".postln;
                    // "server: %".format(server).postln;
                    // server.reorder([Ndef(name)], Group(server));
                    // so this only works when Ndefs are playing on Convenience.nodegroup
                    // });

                    // server.sync;
                    // "convenience group: %".format(nodegroup).postln;
                    // Ndef(name).mold(numChannels).play(out: bus, numChannels: 2, group: nodegroup);
                    // Ndef(name).mold(numChannels).play(out: bus, group: nodegroup);
                    // Ndef(name).play(out: bus, group: Group(server));
                    Ndef(name).mold(numChannels).play(out: bus);
                    "Convenience:: start playing %".format(name).postln;
                    server.sync;
                    server.reorder([Ndef(name)], Group(server));
                };


                //Ndef(name).playN(out)
                //Ndef(name).play(out: bus, numChannels: numChannels).mold(numChannels)

                // if bus is integer going for play out, if bus is a Bus.audio
                // if((bus.class==Bus).not, {
                // 	Ndef(name).mold(numChannels).play(out: bus);
                // 	"Server out".postln;
                // }, {
                // 	"Internal bus".postln;
                // 	// check that the bus is audio rate
                // 	if(bus.rate=='audio', {
                // 		// "is audio rate".postln;
                // 		Ndef(name).mold(numChannels).bus_(bus);
                // 		// Ndef(name).stop()
                // 	})
                // })
            }, {
                // update quant
                Pdef(name).quant_(quant);
                Ndef(name).quant_(quant);
                "Convenience:: % already playing".format(name).postln;
                if(replay.asBoolean, {
                    "Convenience:: retrig %".format(name).postln;
                    // Ndef(name).play(out: bus, numChannels: 2, group: nodegroup);
                    // Ndef(name).play(out: bus, group: nodegroup);
                    // Ndef(name).play(out: bus, group: Group(server));
                    Ndef(name).play(out: bus);
                });
                // mold Ndef if needed
                if((numChannels == Ndef(name).numChannels).not, {
                    //Ndef(name).play(out: bus, numChannels: numChannels).mold(numChannels)
                    "Convenience:: molding Ndef from % to % channels".format(Ndef(name).numChannels, numChannels).postln;
                    Ndef(name).mold(numChannels);
                });

                // if((bus.class == Bus), {
                // 	if((bus.index < Ndef(name).bus.index), {
                // 		"Convenience:: reorder - new group".postln;
                // 		server = server ? Server.default;
                // 		server.reorder([Ndef(name)], Group.new, \addToTail);
                // 		// Ndef(name).play(out: bus);
                // 	});
                // });

                /* if((bus == Ndef(name).outputBusSOMETHING==???).not, {
                    Ndef(name).play(out: bus, numChannels: numChannels).mold(numChannels)
                }); */
            });
            ^Pdef(name); // return the Pdef
        }, {
            "Convenience:: synths not added".postln;
        });
    }

    *repeat { | name, min=0, max=5, repeats = inf |
        var slice_of_history = patterns_history[name].copyRange(min, max);
        Pdef(name, Pbind(*slice_of_history.flopDict(unbubble:false).collect{|x| Pseq(x, repeats)}.asKeyValuePairs));
    }

    *s { | name, fadeTime = 1 ...args |
        if (name.isNil.not and: patterns.includes(name.asSymbol), {
            Ndef(name).source.clear; // aka Pdef(name).stop
            // Ndef(name).stop(fadeTime);
            Ndef(name).free(fadeTime);
            // Ndef(name).clear(fadeTime);
            patterns.remove(name.asSymbol);
        }, {
            "Convenience:: .s not a running pattern".postln
        });
        if (name.isNil.not and: inputs.includes(name.asSymbol), {
            Ndef(name).source.clear; // aka Pdef(name).stop
            Ndef(name).free(fadeTime);
            // Ndef(name).clear(fadeTime);
            inputs.remove(name.asSymbol);
        })
    }

    *sall { | fadeTime = 1 |
        patterns.do{arg name;
            Ndef(name.asSymbol).source.clear; // aka Pdef(name).stop
            Ndef(name.asSymbol).free(fadeTime);
        };
        inputs.do{arg name;
            Ndef(name.asSymbol).free(fadeTime);
            // Ndef(name.asSymbol).clear;
        };
        patterns.clear;
        inputs.clear;
    }

    *record { | name, bus, duration = \inf, format = "wav", server |
        if(name.isNil.not and: (bus.isNil.not), {
            fork{
                var fileName = name.asString ++ "_" ++ Date.localtime.stamp.asString.replace(($ ),"_").replace($:,"");
                var recPath = Platform.recordingsDir ++ "/CR/" ++ fileName ++ "." ++ format; // Convenient Recordings
                var ndefName = (name.asString ++ "_convenient_recorder").asSymbol;
                var num_chans;
                server = server ? Server.default;
                switch ( bus.class.asSymbol,
                    \Integer,  {
                        num_chans = 1;
                        Ndef(ndefName, {
                            SoundIn.ar(bus);
                        });
                    },
                    \Array, {
                        num_chans = bus.size;
                        Ndef(ndefName, {
                            SoundIn.ar(bus);
                        });
                    },
                    \Bus, {
                        num_chans = bus.numChannels;
                        Ndef(ndefName, {
                            In.ar(bus, num_chans);
                        });
                    },
                    {
                        num_chans = 1;
                        Ndef(ndefName, {
                            SoundIn.ar(bus);
                        });
                    }
                );
                // 10.do{server.sync}; // lol, actually works
                1.wait; // hacky, should use condition
                server.record(recPath, Ndef(ndefName).bus, num_chans, Ndef(ndefName).nodeID, duration);
            }
        }, {
            "C: check name and bus please".throw;
        })
    }

    // *pauseRecording { | server |
    //       server = server ? Server.default;
    //       server.pauseRecording;
    // }
    *stopRecording { | server |
        server = server ? Server.default;
        server.stopRecording;
    }

    *pb { | name, buffer, pos=0.0, rate=1.0, trigger, loop=0, bus=#[0,1], amp=0.5 |
    if (name.isNil.not and: buffer.isNil.not) {
        if (rate.isUGen) {
            Ndef(name).map(\rate, rate);
        } {
            Ndef(name).set(\rate, rate);
        };
        if (pos.isUGen) {
            Ndef(name).map(\position, pos);
        } {
            Ndef(name).set(\position, pos);
        };
        if (trigger.isUGen) {
            Ndef(name).map(\trigger, trigger);
        } {
            Ndef(name).set(\trigger, trigger);
        };
        if (amp.isUGen) {
            Ndef(name).map(\amp, amp);
        } {
            Ndef(name).set(\amp, amp);
        };
        Ndef(name).set(\loop, loop);
        Ndef(name, {
            var frames= BufFrames.kr(buffer);
            var sig= ConvenientBufferPlayer.ar(
                numChannels:buffer.numChannels,
                bufnum:buffer,
                rate:\rate.ar(1.0)*BufRateScale.kr(buffer),
                startPos:\position.ar(0.0)*frames,
                trigger:\trigger.ar(1),
                loop:\loop.kr(0)
            );
            Limiter.ar(LeakDC.ar(sig * \amp.ar(1.0)));
        });
        Ndef(name).playN(bus);
        ^Ndef(name);
    } {
        "C:: needs a name / buffer".postln;
    }
    }

    *sb { | name, fadeTime = 0.1 |
        if (name.isNil.not) {
            ^Ndef(name).stop(fadeTime);
        } {
            "C:: needs a name".postln;
        }
    }

    *clear { | name ...args |
        if (name.isNil.not and: patterns.includes(name.asSymbol) or: inputs.includes(name.asSymbol), {
            Ndef(name).source.clear; // aka Pdef(name).stop
            Ndef(name).free;
            Ndef(name).clear;
            if (patterns.includes(name.asSymbol), {
                patterns.remove(name.asSymbol);
            });
            if (inputs.includes(name.asSymbol), {
                inputs.remove(name.asSymbol);
            });
        }, {
            "Convenience:: {}.s not a running Convenience pattern/module".format(name).postln
        })

    }

    *clearAll {
        patterns.do{arg name;
            Ndef(name.asSymbol).source.clear;
            Ndef(name.asSymbol).free;
            Ndef(name.asSymbol).clear;
            patterns.remove(name.asSymbol);
        };
        inputs.do{arg name;
            Ndef(name.asSymbol).source.clear;
            Ndef(name.asSymbol).free;
            Ndef(name.asSymbol).clear;
            inputs.remove(name.asSymbol);
        }
    }

    // *tempo { | name, from, to, secs = 0 |

    // }

    *fxs {
        this.addFxSynths(numFxChannels);
        ^ConvenientCatalog.fxmodules;
    }

    *fxargs { | fxname |
        this.addFxSynths(numFxChannels);
        ^ConvenientCatalog.getFx(fxname.asSymbol).argNames.reject(_ == 'in');
    }

    *pfx { | name, fxs /*, out = #[0,1],  server*/ ...args |
    var fxList, chainSize;

    //server = server ? Server.default;

    fxList = Array.with(*fxs);
    chainSize = fxList.size;


    //"fxs: %".format(fxList).postln;
    //args.do{arg i; i.asSymbol.postln};

    if ((chainSize > 0) and: name.isNil.not and: fxs.isNil.not, {

        // only if a .p has been made
        // if (patterns.includes(name.asSymbol), {

        this.addFxSynths(numFxChannels);

        // if user have not done a *p or did but used *s (*sall) and then went here
        // create Ndef with Pdef source and play it, as if user did a *p
        if (Ndef(name).isPlaying.not, {
            Ndef(name).source = Pdef(name);
            Ndef(name).reshaping = \elastic;
            Ndef(name).play
        });

        "chainSize: %".format(chainSize).postln;

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
        //});
    }, {
        // if (chainSize > 0, {
        // 	"Convenience:: pfx needs a name".postln;
        // }, {
        // 	"Convenience:: pfx needs an array of fx keys".postln;

        // });
        //"Convenience:: pfx needs a name and an array of fx keys".postln;
    })
    ^Ndef(name);
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
                        //if ((key == parameter.asSymbol).not, {
                        pdefn = Pdefn((pattrname.asString++"_"++key.asString).asSymbol).pattern;
                        [key.asSymbol, pdefn]
                        //})
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
                    also Pdefn does not do fadeTime.. only in this way it seems */
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

*ifx { | name, inbus = 0, mul = 0.5, outbus = 0, /* numChannels = 2, */ dur = 1, fxs ...args |
    var fxList, chainSize;

    fxList = Array.with(*fxs);
    chainSize = fxList.size;

    if ((chainSize > 0) and: name.isNil.not and: fxs.isNil.not, {

        this.addFxSynths(numFxChannels);

        inputs.add(name.asSymbol);
        /* 
        Ndef(name);
        Ndef(name).source = {SoundIn.ar(bus, mul)};
        */
        //Ndef(name, {SoundIn.ar(bus, mul)});

        /// some logic for switching between In.ar and Sounds.ar ???
        // check if==integer then SoundIn, if==Bus then In.ar
        Ndef(name)[0] = {SplayAz.ar(numFxChannels, SoundIn.ar(inbus, mul))};

        chainSize.do{ | i |
            if (ConvenientCatalog.fxlist.includesKey(fxList[i].asSymbol), {
                // important to iterate from 1
                // first fx should not be Ndef(\name)[0]
                Ndef(name)[i+1] = \filter -> ConvenientCatalog.getFx(fxList[i].asSymbol);
            }, {
                "Convenience:: fx: % does not exist".format(fxList[i]).postln
            })
        };

        // default add dc filter
        Ndef(name)[chainSize+1] = \filter -> ConvenientCatalog.getFx(\dc);

        // default add limiter
        Ndef(name)[chainSize+2] = \filter -> ConvenientCatalog.getFx(\limiter);

        Ndef(name).play(outbus);

        // last chain entry is control
        Ndef(name)[chainSize+3] = \set -> Pbind(
            \dur, dur,
            *args
        );
    });
    ^Ndef(name);
}

*fade { | name, fadeTime |
    if (patterns.includes(name.asSymbol), {
        //Ndef(name.asSymbol).source.fadeTime_(fadeTime); // ndef soruce -> pdef
        Pdef(name.asSymbol).fadeTime_(fadeTime);
        Ndef(name.asSymbol).fadeTime_(fadeTime);
    });

    if (inputs.includes(name.asSymbol), {
        Ndef(name.asSymbol).fadeTime_(fadeTime);
    });

    if (patterns.includes(name.asSymbol).not and: inputs.includes(name.asSymbol).not, {
        "Convenience:: pattern / ifx module - not running".postln;
    });
}

*crawl { | initpath, depth = 0, force = false, server |
    // if server is nil set to default
    server = server ? Server.default;

    // if no path is specified open crawl drag'n drop window
    if (initpath.isNil, {
        ConvenientCrawlerView.open(depth, server);
    }, {
        // NO WINDOW USAGE
        // initpath was set when crawl method was called
        // going directly to parsing!
        //
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
    //var initPathDepthCount = PathName(initpath).fullPath.withTrailingSlash.split(thisProcess.platform.pathSeparator).size;
    var initPathDepth, anythingFound;

    /*protection against setting an initpath ending with / or // etc,
    which will break the depth control*/
    while({initpath.endsWith("/")}, {
        if(verbose.asBoolean, {"removed /".postln; });
        initpath = initpath[..initpath.size-2]
    });

    // clean out windows seperator
    initpath = initpath.tr(Platform.pathSeparator , $/);

    initPathDepth = PathName(initpath).fullPath.split($/).size;

    server = server ? Server.default;
    dir = initpath; //update getter

    if (verbose, {
        "initDepthCount -> \n\t %".format(initPathDepth).postln;
        "\n__prParseFolders__".post;
        "\n\tinitpath: %".format(initpath).post;
        "\n\tdepth: %\n".format(depth).postln;
    });

    PathName(initpath).deepFiles.do{ | item |
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
      ServerBoot.add(loadFn, server);
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

    if (folderPaths.isEmpty.not,{

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
                if(verbose.asBoolean,{"convenience:: does not understand file channel count".warn});
                nil
            };

            // change this to condition!!
            // server.sync;

            // condition.hang;


            if (working == true, {
                working = false;
                "Convenience::crawl -> done loading single file to dict key \CFiles -> %".format(file.fullPath).postln;
            });

            // pretty hacky kode.. serouslylulyluly ugly
            if(buffers['CFiles'].isNil.not) {
                buffers['CFiles'].add(buffer);
            } {buffers.add('CFiles' -> buffer.asArray);};
            "Conveniece:: single file load:: buffer: % ".format(buffer).postln;
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

*addFxSynths { | server |
    if (ConvenientCatalog.fxsynthsBuild.asBoolean.not,{
        ConvenientCatalog.addFxs(server);
    });
}

*get { | folder, index |
    if (buffers.notEmpty, {
        var bufferGroup;

        // "get siger folder klasse er: %".format(folder.class).postln;
        // "og indeholder: %".format(folder).postln;

        case
        // {folder.isSymbol}{ // wioaw.. not working. jeebz
        {folder.isKindOf(Symbol)} {
            // "folder symbol length %".format(folder.asString.size).postln;
            if (folder.asString.isEmpty)
            {folder = nil; "symbol was empty".postln}
            {
                // if queried folder does not exist
                if (Convenience.buffers.includesKey(folder).not, {
                    "*get:: cant find queried folder: %".format(folder).postln;
                    if(Convenience.folders.asArray[0].isNil.not, {
                        folder = Convenience.folders.asArray[0];
                        "*get::replacing with: %".format(folder).postln;
                    }, {
                        Error("Convenience::*get:: user is asking for folder which is not there, and *get cant find another folder to replace it with").throw;
                        ^nil
                    })
                });
            };
        }
        // {folder.isString}{
        //     if(Convenience.folders.asArray[0].isNil.not) {
        //         folder = Convenience.folders.asArray[0]};
        //     "C :: folder specified as string -> not recognized".postln;
        // }
        // {folder.isKindOf(Char)}{
        //     if(Convenience.folders.asArray[0].isNil.not) {
        //         folder = Convenience.folders.asArray[0]};
        //     "C :: folder specified as char -> not recognized".postln;
        // }
        {folder.isInteger}{
            folder = this.folderNum(folder)
        }
        {folder.isFloat}{
            folder = this.folderNum(folder.round.asInteger)
        }
        {folder.isNil}{
            if(Convenience.folders.asArray[0].isNil.not, {
                folder = Convenience.folders.asArray[0];
            }, {
                Error("Conveience::*get:: folder is unspecified, which is okay but *get cant find a folder to use").throw;
                ^nil
            })
        }
        {"C:: folder can not be specified in that type..".warn};

        bufferGroup = buffers[folder.asSymbol];

        if (index.isFloat) {index.round.asInteger}; // Float to Integer
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

    ^nil
}

*files {
    ^buffers.keysValuesDo { | folderName, bufferArray |
        bufferArray.do { | buffer | 
            "% -> %".format(folderName, buffer).postln
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

    if (gui.asBoolean == true, {
        listWindow = ConvenientListView.open;
    });
}

*prKeyify { | input |
    var result;
    if (input == "r" or: {input == "rest"}) { input = input[0].toUpper++input[1..] };
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

        // ~folder.postln;
        // if buffer is not directly used
        // use folder and index references
        if (~buffer.isNil) {
            var folder = ~folder;
            var index = ~index;
            ~buffer = Convenience.get(folder, index);
            // "C event folder is -> %".format(folder).postln;
        };

        bufferNumChannels = ~buffer.numChannels;
        //"eventType found % bufferNumChannels".format(bufferNumChannels).postln;

        outputNumChannels = ~numChannels;
        //outputNumChannels.postln;

        scaling = ~tuningOnOff;
        if(scaling.isNil) {scaling = 0};
        pitchshift = ~pst;
        if(pitchshift.isNil) {pitchshift = 0};

        // get instrument
        case
        {pitchshift == 1 and: scaling == 0} {
            switch(bufferNumChannels,
                1, {
                    ~instrument = ("ConvenienceMonoPitchShift_"++outputNumChannels).asSymbol;
                },
                2, {
                    ~instrument = ("ConvenienceStereoPitchShift_"++outputNumChannels).asSymbol;
                },
                {
                    ~instrument = ("ConvenienceMonoPitchShift_"++outputNumChannels).asSymbol;
                }
            );
        }
        {pitchshift == 1 and: scaling == 1} {
            switch(bufferNumChannels,
                1, {
                    ~instrument = ("ConvenienceMonoPitchShiftScale_"++outputNumChannels).asSymbol;
                },
                2, {
                    ~instrument = ("ConvenienceStereoPitchShiftScale_"++outputNumChannels).asSymbol;
                },
                {
                    ~instrument = ("ConvenienceMonoPitchShiftScale_"++outputNumChannels).asSymbol;
                }
            );
        }
        {scaling == 1 and: pitchshift == 0} {
            switch(bufferNumChannels,
                1, {
                    ~instrument = ("ConvenienceMonoScale_"++outputNumChannels).asSymbol;
                },
                2, {
                    ~instrument = ("ConvenienceStereoScale_"++outputNumChannels).asSymbol;
                },
                {
                    ~instrument = ("ConvenienceMonoScale_"++outputNumChannels).asSymbol;
                }
            );

        }
        {
            switch(bufferNumChannels,
                1, {
                    ~instrument = ("ConvenienceMono_"++outputNumChannels).asSymbol;
                },
                2, {
                    ~instrument = ("ConvenienceStereo_"++outputNumChannels).asSymbol;
                },
                {
                    ~instrument = ("ConvenienceMono_"++outputNumChannels).asSymbol;
                }
            );
        };

        ~type = \note;
        ~bufnum = ~buffer.bufnum;
        currentEnvironment.play
    });
}

*prFileToLoadFunc{ | filepath, server | 
    var special_key='CFiles';
    // clean out windows seperator
    filepath = filepath.tr(Platform.pathSeparator , $/);
    server = server ? Server.default;

    if (verbose, {
        "\n\tfile path: %".format(filepath).postln;
    });

    // TODO(salkin-mada): 
    // check if file is supportedExtensions
    // check if already present in filePaths
    //
    filePaths.add(special_key -> filepath.asSymbol);

    // stage work for boot up
    // ServerBoot.add(loadFn, server);

    // if server is running create rightaway
    if (server.serverRunning) {
        var news = false;

        // TODO(salkin-mada): 
        // THIS is not DONE!!!
        // all signle files is new all the time... 
        // have to check filePaths against buffer paths . news..
        // filePaths.keysValuesDo{ | key, values |
        // values.do{ | val |
        // buffers['CFiles'].do{ | item | 
        // if((item.path == PathName(val).fullPath).not) {
        news = true;
        // };
        // if (buffers.includes(val).not, {news = true});
        // }
        // }
        // };


        if (filePaths.isEmpty.not,{

            if (news,{
                filePaths.keysValuesDo{ | key, path |
                    {
                        this.load(path.asString, "file", server);
                    }.fork(AppClock);
                };
                if (verbose, {"Convenience:: prFileToLoadFunc loading done".postln})

            }, {
                "Convenience::crawl -> file with that name has already been loaded".postln;
            });
        }, {"Convenience::crawl -> no file paths".postln});
    } {"Convenience:: Server not running - needed for single file loads".postln};
}


// fft method for selecting filter bands
// thanks to Bálint Laczkó
//*buckets {| frame = 1024, numBands = 4, band = 0 |
//	var result, coeff, binLow, binHigh;

//	//snap frame to power of 2
//	frame = 2**frame.log2.round;

//	//numBands should not be lower than 1
//	if(numBands < 1, {numBands = 1}, {numBands});

//	//selected band index clipped into sensible range
//	band = band.clip(0, numBands -1);

//	//the coefficient for calculating logarithmically scaled bands
//	//(the same equation as used for equal temperament)
//	coeff = (frame / 2)**(1 / numBands);

//	//calculate the "bin-range" of the selected band
//	binLow = ((coeff**band).round - 1).clip(0, (frame / 2) - 1);
//	if(band == (numBands - 1), {
//		binHigh = ((coeff**(band + 1)).round - 1).clip(binLow, (frame / 2) - 1)
//	}, {binHigh = ((coeff**(band + 1)).round - 2).clip(binLow, (frame / 2) - 1)
//});
//result = [binLow, binHigh];
////return the bin-range in an array
//^result
//}

}
