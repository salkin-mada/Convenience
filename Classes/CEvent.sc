+ C {
  // *addEventType {
  //   Event.addEventType(\Convenience, {
  //     var bufferNumChannels, outputNumChannels, scaling, pitchshift;

  //     // ~folder.postln;
  //     // if buffer is not directly used
  //     // use folder and index references
  //     if (~buffer.isNil) {
  //       var folder = ~folder;
  //       var index = ~index;
  //       ~buffer = Convenience.get(folder, index);
  //       // "C event folder is -> %".format(folder).postln;
  //     };

  //     bufferNumChannels = ~buffer.numChannels;
  //     //"eventType found % bufferNumChannels".format(bufferNumChannels).postln;

  //     outputNumChannels = ~numChannels;
  //     //outputNumChannels.postln;

  //     scaling = ~tuningOnOff;
  //     if(scaling.isNil) {scaling = 0};
  //     pitchshift = ~pst;
  //     if(pitchshift.isNil) {pitchshift = 0};

  //     // get instrument
  //     case
  //     {pitchshift == 1 and: scaling == 0} {
  //       switch(bufferNumChannels,
  //         1, {
  //           ~instrument = ("ConvenienceMonoPitchShift_"++outputNumChannels).asSymbol;
  //         },
  //         2, {
  //           ~instrument = ("ConvenienceStereoPitchShift_"++outputNumChannels).asSymbol;
  //         },
  //         {
  //           ~instrument = ("ConvenienceMonoPitchShift_"++outputNumChannels).asSymbol;
  //         }
  //       );
  //     }
  //     {pitchshift == 1 and: scaling == 1} {
  //       switch(bufferNumChannels,
  //         1, {
  //           ~instrument = ("ConvenienceMonoPitchShiftScale_"++outputNumChannels).asSymbol;
  //         },
  //         2, {
  //           ~instrument = ("ConvenienceStereoPitchShiftScale_"++outputNumChannels).asSymbol;
  //         },
  //         {
  //           ~instrument = ("ConvenienceMonoPitchShiftScale_"++outputNumChannels).asSymbol;
  //         }
  //       );
  //     }
  //     {scaling == 1 and: pitchshift == 0} {
  //       switch(bufferNumChannels,
  //         1, {
  //           ~instrument = ("ConvenienceMonoScale_"++outputNumChannels).asSymbol;
  //         },
  //         2, {
  //           ~instrument = ("ConvenienceStereoScale_"++outputNumChannels).asSymbol;
  //         },
  //         {
  //           ~instrument = ("ConvenienceMonoScale_"++outputNumChannels).asSymbol;
  //         }
  //       );

  //     }
  //     {
  //       switch(bufferNumChannels,
  //         1, {
  //           ~instrument = ("ConvenienceMono_"++outputNumChannels).asSymbol;
  //         },
  //         2, {
  //           ~instrument = ("ConvenienceStereo_"++outputNumChannels).asSymbol;
  //         },
  //         {
  //           ~instrument = ("ConvenienceMono_"++outputNumChannels).asSymbol;
  //         }
  //       );
  //     };

  //     ~type = \note;
  //     ~bufnum = ~buffer.bufnum;
  //     currentEnvironment.play
  //   });
  // }
}
