Convenience {
  // user config
  classvar >loadSynths = true; // should crawler auto load synths
  classvar <>verbose = false; // debug or interest
  classvar <>numFxChannels = 2; // default number of fx channels
  classvar <>maxChannelArraySize = 8; // see Core/BufferPlayers.sc
  classvar <>pattern_history_size = 100; // for *repeat
  classvar <>restrictRecFormats = true;
  classvar <>cFolder = 'CFiles';

  // private config
  classvar <dir, <buffers, <folderPaths, <filePaths, <patterns, <pattern_properties, <patterns_history, <inputs;
  classvar tmpName;
  classvar <loadFn;
  classvar listWindow;
  classvar recorder;
  classvar <globChars = "[]";
  // classvar <nodegroup;

  classvar working = false;

  const <supportedExtensions = #[\wav, \wave, \aif, \aiff, \aifc, \flac, \ogg];

  *initClass { | server |
    server = server ? Server.default;
    buffers = Dictionary.new;
    folderPaths = Dictionary.new;
    filePaths = Dictionary.new;
    patterns = IdentitySet.new;
    pattern_properties = Dictionary.new;
    patterns_history = Dictionary.new;
    inputs = IdentitySet.new;
    recorder = Recorder.new(server);
    loadFn = #{ | server | C.prPipeFoldersToLoadFunc(server: server)};
    // loadFileFn = #{ | server | this.prFileToLoadFunc(server: server)};
    this.prAddEventType;
    "\n*** Convenience enabled ***".postln;
    if(Platform.ideName=="scnvim", {
      "... -.-. -. ...- .. --".postln;
    });
    // group for Convenience Ndefs
    // server.doWhenBooted{
    // 	nodegroup = Group.new; "adding group for Convenience".postln
    // }
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


  *bus { | name |
    ^Ndef(name.asSymbol).bus
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
        Error("Conveience::*get:: folder is unspecified -> using default").throw;
        ^nil
      })
    }
    {"C:: folder can not be specified in that type.. supported types are: symbol, integer, float".warn};

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

    if (~legato.isNil) {~legato = 1.0};
    ~attack = ~attack*~legato;
    ~sustain = ~sustain*~legato;
    ~release = ~release*~legato;

    ~type = \note;
    ~bufnum = ~buffer.bufnum;
    currentEnvironment.play
  });
}

}
