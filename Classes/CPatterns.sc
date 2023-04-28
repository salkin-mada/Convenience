+ C {
    *p { | name, eventOrDict |
        var pdefnProperties = List.new;
        name = name.asSymbol;

        if (ConvenientCatalog.synthsBuild, {

            if(name.isNil,{"needs a key aka name, please".throw; ^nil});
            // check that seed is not a pattern? only eating integers

            pattern_properties[name] = ConvenientOrderedIdentityDictionary.newFromAssociationArray(this.properties++eventOrDict.asAssociations).reject{|v,k|k=='name'};

            pattern_properties[name][\server] = pattern_properties[name][\server] ? Server.default;
            pattern_properties[name][\tempo] = TempoClock(pattern_properties[name][\tempo] ? 1.0);
            pattern_properties[name][\type] = pattern_properties[name][\type] ? \Convenience;
            pattern_properties[name][\scale] = pattern_properties[name][\scale] ? Scale.chromatic;
            pattern_properties[name][\numChannels] = pattern_properties[name][\numChannels] ? 2;

            // if folder is unspecified in Convenience.p func
            // for some reason .p does not work with eventType and going to .get
            // if this is not here
            if (pattern_properties[name][\folder].isNil, {
                if(Convenience.folders.asArray[0].isNil.not, {
                    pattern_properties[name][\folder] = Convenience.folders.asArray[0];
                }, {Error("Convenience:: no buffers available").throw; ^nil})
            });
            // dont allow (change) strings in folder argument
            if (pattern_properties[name][\folder].isString) {pattern_properties[name][\folder] = pattern_properties[name][\folder].asSymbol};

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

            if((patterns.includes(pattern_properties[name].asSymbol)).not, {
                // check if Ndef already exists by looking up all Ndefs on given server
                // and checking that it is a Ndef (dictFor will return ProxySpace.nil if Ndef.key is not present)
                // therefor check with isKindOf
                // if(Ndef.dictFor(pattern_properties[name][\server])[pattern_properties[name].asSymbol].isKindOf(Ndef), {
                //     "some NodeProxy Definition called: % already exists".format(pattern_properties[name]).warn;
                //     ^nil
                // }, {
                    // add to Convenience pattern set reference
                    if(verbose) {"new pattern: %".format(name).postln};
                    patterns.add(name);
                // })
            });

            pdefnProperties = pattern_properties[name].reject{|val,key|key=='quant' or: (key=='tempo') or: (key=='server')}.collect{ | val,key |
                var pdefn, value;
                if (verbose) {"%: %".format(key,val).postln};
                // allow empty symbols in folder property
                if (key == 'folder') {
                    // if (folder.asString.isEmpty) {
                    if (key.asString.isEmpty) {
                        key = nil;
                        if (verbose) {"C:: folder symbol is empty..".postln};
                    }
                };

                switch ( pattern_properties[name][key].class,
                // wrap Ndef bus input in Pfunc
                Ndef,  {
                    // TODO: check if it is Ndef with .bus call?
                    // value = Pfunc{ pattern_properties[name][k].bus.asMap };
                    value = Pfunc{ eventOrDict.asDict[key].bus.asMap };
                    // if (key == 'index') {
                    //     value = value.asInteger;
                    // }
                    pdefn = Pdefn((name.asString++"_"++key.asString).asSymbol, value)/* .pattern */;
                },
                {
                  // default
                    value = pattern_properties[name][key];
                    pdefn = Pdefn((name.asString++"_"++key.asString).asSymbol, value)/* .pattern */;
                    pdefn
                });

                // "%: %".format(key, value).postln;

                // if (value.isNil.not) {
                    // [k.asSymbol, pdefn];
                // } {
                //     "NILL 2".postln;
                //     [key.asSymbol, nil] // the clever thing?
                // }
                //}
            };

            // pdefnProperties.do{|k,v| "---- %: %".format(k,v).postln};

            if(patterns_history.includesKey(name).not) {
                patterns_history.add(name.asSymbol -> List.new);
                if(verbose) {"adding pattern history list for %".format(name).postln};
            };

            /* if (verbose) {
              "\bproperties (this.properties++dict)\n".postln;
              pattern_properties[name].keysValuesDo{|k,v|"%: %".format(k,v).postln};
              "\npdefnProperties\n".postln;
              pdefnProperties.do{|pair|"%".format(pair).postln};
            }; */

            Pdef(name,
                Pseed(pattern_properties[name][\seed],
                Pbind().patternpairs_(pdefnProperties.asKeyValuePairs.flat).collect{|env|
                    if(patterns_history[name].size >= pattern_history_size, {
                        patterns_history[name].removeAt(0);
                    });
                    patterns_history[name].add(env);
                    env;
                } 
            )
        ).play(quant: pattern_properties[name][\quant]);
    // Pdef(name).play;
    // if ((Ndef(name).isPlaying).not, {
    //     // Ndef(properties[\name]).source = Pdef(properties[\name]).clock_(AppClock);
    //     Ndef(name).source = Pdef(name);
    //     Ndef(name).reshaping = \elastic;
    //     Pdef(name).quant_(pattern_properties[event.asDict[\quant]]);
    //     Ndef(name).quant_(pattern_properties[event.asDict[\quant]]);
    //     // også tjek for brug af Ndef().isMonitoring
    //     fork{
    //         // if((patterns.includes(name.asSymbol)).not, {
    //         // reorder Ndef (fix for using Bus.audio as .p(bus) arg, IF Bus was created before this Convenience pattern/ndef)
    //         // hacky.. bad programming? some fundamental sc misunderstading here?
    //         // "Convenience:: reorder - group".postln;
    //         // "server: %".format(server).postln;
    //         // server.reorder([Ndef(name)], Group(server));
    //         // so this only works when Ndefs are playing on Convenience.nodegroup
    //         // });

    //         // server.sync;
    //         // "convenience group: %".format(nodegroup).postln;
    //         // Ndef(name).mold(numChannels).play(out: bus, numChannels: 2, group: nodegroup);
    //         // Ndef(name).mold(numChannels).play(out: bus, group: nodegroup);
    //         // Ndef(name).play(out: bus, group: Group(server));
    //         Ndef(name).mold(pattern_properties[name][\numChannels]).play(out: pattern_properties[name][\bus]);
    //         if (verbose) {
    //           "Convenience:: start playing %".format(name).postln;
    //         };
    //         // pattern_properties[name][\server].sync;
    //         // pattern_properties[name][\server].reorder([Ndef(name)], Group(pattern_properties[name][\server]));
    //     };

    //     //Ndef(name).playN(out)
    //     //Ndef(name).play(out: bus, numChannels: numChannels).mold(numChannels)

    //     // if bus is integer going for play out, if bus is a Bus.audio
    //     // if((bus.class==Bus).not, {
    //     // 	Ndef(name).mold(numChannels).play(out: bus);
    //     // 	"Server out".postln;
    //     // }, {
    //     // 	"Internal bus".postln;
    //     // 	// check that the bus is audio rate
    //     // 	if(bus.rate=='audio', {
    //     // 		// "is audio rate".postln;
    //     // 		Ndef(name).mold(numChannels).bus_(bus);
    //     // 		// Ndef(name).stop()
    //     // 	})
    //     // })
    // }, {
    //     // update quant
    //     Pdef(name).quant_(pattern_properties[name][\quant]);
    //     Ndef(name).quant_(pattern_properties[name][\quant]);
    //     if (verbose) {
    //       "Convenience:: % already playing".format(name).postln;
    //     };
    //     if(pattern_properties[name][\replay].asBoolean, {
    //       if (verbose) {
    //         "Convenience:: retrig %".format(name).postln;
    //       };
    //         // Ndef(name).play(out: bus, numChannels: 2, group: nodegroup);
    //         // Ndef(name).play(out: bus, group: nodegroup);
    //         // Ndef(name).play(out: bus, group: Group(server));
    //         Ndef(name).play(out: pattern_properties[name][\bus]);
    //     });
    //     // mold Ndef if needed
    //     if((pattern_properties[name][\numChannels] == Ndef(name).numChannels).not, {
    //         //Ndef(name).play(out: bus, numChannels: numChannels).mold(numChannels)
    //         if (verbose) {
    //           "Convenience:: molding Ndef from % to % channels".format(Ndef(name).numChannels, pattern_properties[name][\numChannels]).postln;
    //         };
    //         Ndef(name).mold(pattern_properties[name][\numChannels]);
    //     });

    //     if((bus.class == Bus), {
    //         if((bus.index < Ndef(name).bus.index), {
    //             if (verbose) {
    //                 "Convenience:: reorder - new group".postln;
    //             };
    //     		pattern_properties[name][\server] = pattern_properties[name][\server] ? Server.default;
    //     		pattern_properties[name][\server].reorder([Ndef(name)], Group.new, \addToTail);
    //     		// Ndef(name).play(out: bus);
    //     	});
    //     });

        /* if((bus == Ndef(name).outputBusSOMETHING==???).not, {
            Ndef(name).play(out: bus, numChannels: numChannels).mold(numChannels)
          }); */
        // });
        // ^Pdef(name); // return the Pdef
        ^C // return self
      }, {
        "Convenience:: synths not added".warn;
        Error.throw;
      });
    }

    *repeat { | name, min=0, max=5, repeats = inf |
        var slice_of_history, min_, max_;
        // min_ = this.pattern_history_size-min_;
        // max_ = min_-max_;
        // slice_of_history = patterns_history[name].copyRange(max_, min_);
        slice_of_history = patterns_history[name].copyRange(min, max);
        Pdef(name, Pbind(*slice_of_history.flopDict(unbubble:false).collect{|x| Pseq(x, repeats)}.asKeyValuePairs));
    }

    *s { | name, fadeTime = 1 ...args |
        if (name.isNil.not and: patterns.includes(name.asSymbol), {
            Pdef(name).stop();
            patterns.remove(name.asSymbol);
            pattern_properties.put(name.asSymbol, nil);
        }, {
            "Convenience:: .s not a running pattern".postln
        });
    }

    *sall { | fadeTime = 1 |
        patterns.do{arg name;
            Pdef(name).stop
        };
        patterns.clear;
        pattern_properties.clear;
    }

    *pp { | patternName, parameter, value |
        if ((patternName.isNil and: parameter.isNil).not, {
            // if (Pdef.all.includesKey(patternName.asSymbol), {
            if ((parameter=='quant' or: (parameter=='tempo') or: (parameter=='server')).not) {
                if (patterns.includes(patternName.asSymbol), {
                    if (this.pattern_properties[patternName].includesKey(parameter.asSymbol), {
                        if (value.isNil.not, {
                            // var pdefnProperties = List.new;
                            var pdefn;

                            // Pdefn((patternName.asString++"_"++parameter).asSymbol, value);

                            // pattern_properties[patternName].keysDo{ | key |
                            //     if (((key == \name) or: (key == \quant) or: (key == \tempo)).not, {
                            //         pdefnProperties.add(key)
                            //     })
                            // };

                            // pdefnProperties = pdefnProperties.collect{ | key |
                            //     var pdefn;
                            //     //if ((key == parameter.asSymbol).not, {
                            //     pdefn = Pdefn((patternName.asString++"_"++key.asString).asSymbol).pattern;
                            //     [key.asSymbol, pdefn]
                            //     //})
                            // };

                            // Pdef(patternName,
                            //     Pseed(pattern_properties[patternName][\seed],
                            //     Pbind().patternpairs_(pdefnProperties.collect{ | pair |
                            //         pair.postln;
                            //         pair;
                            //     }.flat)
                            // ));

                            switch ( parameter.class,
                                Ndef,  {
                                    value = Pfunc{ value.bus.asMap };
                                },
                                {
                                    // default
                                }
                            );

                            // pdefn = Pdefn((patternName.asString++"_"++parameter.asString).asSymbol, value).pattern;
                            pdefn = Pdefn((patternName.asString++"_"++parameter.asString).asSymbol, value);

                            // Pbindef(patternName, parameter.asSymbol, pdefn);
                            ^pdefn
                            // ^C

                            // fadeTime hack end
                            /* Pbindef only has Pdef's fadeTime when it incrementally
                            changed from a Pdef the first time.
                            also Pdefn does not do fadeTime.. only in this way it seems */
                        }, {
                            // ^Pdefn((patternName.asString++"_"++parameter).asSymbol).pattern
                            ^Pdefn((patternName.asString++"_"++parameter).asSymbol)
                        })
                    }, {
                        "C::pp -> param does not exist".postln
                    })
                }, {
                    "C::pp -> pattrname does not exist".postln
                })

            } {
                "C::pp -> quant, tempo and server is not *pp able".warn;
            }
        }, {
            "C::pp -> needs a pattrname and param".postln
        })
    }
}
