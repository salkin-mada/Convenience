+ C {

    *rec { | name, bus, duration = \inf, format = "wav"/* , server */ |

        if(restrictRecFormats) {
            if(supportedExtensions.includes(format.toLower.asSymbol).not) {"C::format not supported".warn; ^nil};
        };

        if(name.isNil.not and: (bus.isNil.not), {
            fork{
                var fileName = name.asString ++ "_" ++ Date.localtime.stamp.asString.replace(($ ),"_").replace($:,"");
                var recPath = Platform.recordingsDir ++ "/CR/" ++ fileName ++ "." ++ format; // Convenient Recordings
                var ndef, ndefName = (name.asString ++ "_convenient_recorder").asSymbol;
                // var num_chans;
                // server = server ? Server.default;

                switch ( bus.class.asSymbol,
                    \Integer,  {
                        // num_chans = 1;
                        ndef = Ndef(ndefName, {
                            SoundIn.ar(bus);
                        });
                    },
                    \Array, {
                        // num_chans = bus.size;
                        ndef = Ndef(ndefName, {
                            SoundIn.ar(bus);
                        });
                    },
                    \Bus, {
                        // num_chans = bus.numChannels;
                        ndef = Ndef(ndefName, {
                            // In.ar(bus, num_chans);
                            In.ar(bus, bus.numChannels);
                        });
                    },
                    \Symbol, {
                        ndefName = (bus.asString ++ "_convenient_recorder").asSymbol;
                        bus = Ndef(bus).bus;
                        // num_chans = bus.numChannels;
                        ndef = Ndef(ndefName, {
                            // In.ar(bus, num_chans);
                            In.ar(bus, bus.numChannels);
                        });
                    },
                    {
                        "C::record (bus) not satisfied".warn;
                        ^nil
                        // num_chans = 1;
                        // ndef = Ndef(ndefName, {
                        //     SoundIn.ar(bus);
                        // });
                    }
                );
                // 10.do{server.sync}; // lol, actually works
                1.wait; // hacky, should use condition
                // server.record(recPath, Ndef(ndefName).bus, num_chans, Ndef(ndefName).nodeID, duration);
                // this.recorder.record(recPath, Ndef(ndefName).bus, num_chans, Ndef(ndefName).nodeID, duration);
                recorder.record(recPath, ndef.bus, ndef.numChannels, ndef.nodeID, duration);
            }
        }, {
            "C: check name and bus please".throw;
        })
    }

    *prec {/*  | server | */
        // server = server ? Server.default;
        // server.pauseRecording;
        recorder.stopRecording;
    }

    *srec {/*  | server | */
        // server = server ? Server.default;
        // server.stopRecording;
        recorder.stopRecording;
    }

}
