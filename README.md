# salk

### exercise
```supercollider
s.doWhenBooted{
    forkIfNeeded{
        if(topEnvironment.includesKey('core'), {
            topEnvironment.at('core').free;
        });
        s.sync;
        topEnvironment.put(
            'core', Buffer.read(s, "salk.wav")
        );
        s.sync;

        "salk".postln;
    };

```
