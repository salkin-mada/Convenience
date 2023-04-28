// + C {

//   *fxs {
//     this.addFxSynths(numFxChannels);
//     ^ConvenientCatalog.fxmodules;
//   }

//   *fxargs { | fxname |
//     this.addFxSynths(numFxChannels);
//     ^ConvenientCatalog.getFx(fxname.asSymbol).argNames.reject(_ == 'in');
//   }

//   *pfx { | name, fxs /*, out = #[0,1],  server*/ ...args |
//   var fxList, chainSize;

//   //server = server ? Server.default;

//   fxList = Array.with(*fxs);
//   chainSize = fxList.size;


//   //"fxs: %".format(fxList).postln;
//   //args.do{arg i; i.asSymbol.postln};

//   if ((chainSize > 0) and: name.isNil.not and: fxs.isNil.not, {

//     // only if a .p has been made
//     // if (patterns.includes(name.asSymbol), {

//     this.addFxSynths(numFxChannels);

//     // if user have not done a *p or did but used *s (*sall) and then went here
//     // create Ndef with Pdef source and play it, as if user did a *p
//     if (Ndef(name).isPlaying.not, {
//       Ndef(name).source = Pdef(name);
//       Ndef(name).reshaping = \elastic;
//       Ndef(name).play
//     });

//     "chainSize: %".format(chainSize).postln;

//     chainSize.do{ | i |
//       ///// not working value.calss,.  how to get fx key??? hmm..
//       //if ((Ndef((name.asString).asSymbol)[i+1].value.class == fxs[i]).not, {
//       //fxList[i].postln;


//       if (ConvenientCatalog.fxlist.includesKey(fxList[i].asSymbol), {
//         // important to iterate from 1
//         // first fx should not be Ndef(\name)[0]
//         Ndef(name)[i+1] = \filter -> ConvenientCatalog.getFx(fxList[i].asSymbol);
//       }, {
//         "Convenience:: fx: % does not exist".format(fxList[i]).postln
//       })
//       //})
//     };

//     // default add dc filter
//     Ndef(name)[chainSize+1] = \filter -> ConvenientCatalog.getFx(\dc);

//     // default add limiter
//     Ndef(name)[chainSize+2] = \filter -> ConvenientCatalog.getFx(\limiter);

//     // last chain entry is control
//     Ndef(name)[chainSize+3] = \set -> Pbind(
//       \dur, Pdefn((name++"_dur").asSymbol),
//       *args
//     );
//     //});
//   }, {
//     // if (chainSize > 0, {
//     // 	"Convenience:: pfx needs a name".postln;
//     // }, {
//     // 	"Convenience:: pfx needs an array of fx keys".postln;

//     // });
//     //"Convenience:: pfx needs a name and an array of fx keys".postln;
//   })
//   ^Ndef(name);
// }

// *ifx { | name, inbus = 0, mul = 0.5, outbus = 0, /* numChannels = 2, */ dur = 1, fxs ...args |
//   var fxList, chainSize;

//   fxList = Array.with(*fxs);
//   chainSize = fxList.size;

//   if ((chainSize > 0) and: name.isNil.not and: fxs.isNil.not, {

//     this.addFxSynths(numFxChannels);

//     inputs.add(name.asSymbol);
//     /* 
//     Ndef(name);
//     Ndef(name).source = {SoundIn.ar(bus, mul)};
//     */
//     //Ndef(name, {SoundIn.ar(bus, mul)});

//     /// some logic for switching between In.ar and Sounds.ar ???
//     // check if==integer then SoundIn, if==Bus then In.ar
//     Ndef(name)[0] = {SplayAz.ar(numFxChannels, SoundIn.ar(inbus, mul))};

//     chainSize.do{ | i |
//       if (ConvenientCatalog.fxlist.includesKey(fxList[i].asSymbol), {
//         // important to iterate from 1
//         // first fx should not be Ndef(\name)[0]
//         Ndef(name)[i+1] = \filter -> ConvenientCatalog.getFx(fxList[i].asSymbol);
//       }, {
//         "Convenience:: fx: % does not exist".format(fxList[i]).postln
//       })
//     };

//     // default add dc filter
//     Ndef(name)[chainSize+1] = \filter -> ConvenientCatalog.getFx(\dc);

//     // default add limiter
//     Ndef(name)[chainSize+2] = \filter -> ConvenientCatalog.getFx(\limiter);

//     Ndef(name).play(outbus);

//     // last chain entry is control
//     Ndef(name)[chainSize+3] = \set -> Pbind(
//       \dur, dur,
//       *args
//     );
//   });
//   ^Ndef(name);
// }

// }
