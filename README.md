# Salk

### ...

### at the moment this readme file acts as a sticky note.

```supercollider
play{d=SinOsc;x=PinkNoise.ar([d.ar(2.01).abs,d.ar(2).abs]);SinGrain.ar(Dust.kr(1),10,x.range(50,500),0.1)};
```
```supercollider
play{d=SinOsc;x=PinkNoise.ar([d.ar(2.01).abs,d.ar(2).abs]);f=3*(x*2e3)+x.range(-2,2);SinGrain.ar(Dust.kr(0.1),60,x.range(50,f),0.1)};//wait
```

### topEnvironment.put
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
