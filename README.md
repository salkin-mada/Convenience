# salk

### learning to make Ugens ..

### and markdown .. therefore, at the moment, this readme file acts as a sticky note.
```supercollider
s.doWhenBooted{
    forkIfNeeded{
        if(topEnvironment.includesKey('core'), {
            topEnvironment.at('core').free;
        });
        s.sync;
        topEnvironment.put(
            'core', SalkBuffer.read(s, "salk.wav")
        );
        s.sync;

        "salk".postln;
    };
};
```
