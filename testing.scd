/*/-/-/-/-/             |
  /-/-/-/-/         /---/---/
      |                 x         |
   /--/--/              x     /---/---/
      x       .       --x--       x
     -x       |         x         x
      x       |       __x         x
      x_______|_______||x         x
     /                ||x\      /   \
    /----------------/||x \   /___ ___\
   |         ##     | ||x  | ||  | |  ||
   |         ##     | ||x  | ||  | |  ||
-------------//---------------------------*/


s.recChannels= 2;
s.recChannels;
Server.recChannels = 2;
s.makeWindow


GreenPeace.activate
GreenPeace.deactivate

// oneliners for recording some sounds for use in the Buffer
// needs the sc3-plugins
play{d=SinOsc;x=PinkNoise.ar([d.ar(2.01).abs,d.ar(2).abs]);SinGrain.ar(Dust.kr(1),10,x.range(50,500),0.1)};

play{d=SinOsc;x=PinkNoise.ar([d.ar(2.01).abs,d.ar(2).abs]);f=12*(x*2e4)+x.range(-2,2);SinGrain.ar(Dust.kr(0.2),10,x.range(50,f),0.1)};//wait
play{d=SinOsc;x=PinkNoise.ar([d.ar(2.01).abs,d.ar(2).abs]);f=12*(x*2e4)+x.range(-2,2);SinGrain.ar(Dust.kr(0.2),30,x.range(50,f),0.1)};//wait
play{d=SinOsc;x=PinkNoise.ar([d.ar(2.01).abs,d.ar(2).abs]);f=3*(x*2e3)+x.range(-2,2);SinGrain.ar(Dust.kr(0.1),60,x.range(50,f),0.1)};//wait

// works out of ze box
play{{({|i|x=i+1;y=LFNoise2.ar(0.1);f=77*(x/2)+y.range(-2,2);LPF.ar(Saw.ar(f,y*(Line.ar(0,0.2,99.rand)/(x*0.3))),f*3)}!rrand(9,28)).sum}!2}
play{{({|i|x=i+1;y=LFDNoise1.ar(0.1);f=120*(x/pi)+y.range(-5,5);LPF.ar(Saw.ar(f,y*(Line.ar(0,0.2,120.rand)/(x*0.1))),f*3)}!rrand(20,40)).sum}!2}
play{{({|i|x=i+1;y=LFNoise2.ar(0.1);f=120*(x/2)+y.range(-2,2);LPF.ar(Saw.ar(f,y*(Line.ar(0,0.2,99.rand)/(x*0.3))),f*3)}!rrand(1,2)).sum}!2}


/////////////////////////////////////
/////////////////////////////////////
/////////////////////////////////////
// stolen from the RedGrain .


~length= 1;        //segment lengths in seconds - should be the same for positions to scan correctly
~b1= SalkBuffer(s, "C:/Users/na/Desktop/live projects/gesture non falcon & pitch possessions/snip for redGrain.wav", 0.1, ~length);
~b2= SalkBuffer(s, "C:/Users/na/Desktop/live projects/gesture non falcon & pitch possessions/snip for redGrain.wav", 0.3, ~length);
~b3= SalkBuffer(s, "C:/Users/na/Desktop/live projects/gesture non falcon & pitch possessions/snip for redGrain.wav", 0.5, ~length);

//-- create and start the granulator object
t = SalkFR(s);
t.buf_(~b1);
t.start;

//offset halfway into source buffer
t.pos_(0.5);

//switch buffer
t.buf_(~b2);

//cycle through buffers quickly 20 times
(
fork{
    20.do{|i| t.buf_([~b1, ~b2, ~b3].wrapAt(i)); 0.1.wait};
};
)


(
fork{
    1.do{|i|
            8.do{|i|
                    t.buf_([~b1, ~b2, ~b3].wrapAt(i));
                    t.rate_(2.0.rand2);
                    0.4.wait;
            };
        5.wait;
        5.do{|i|
                    t.buf_([~b1, ~b2, ~b3].wrapAt(i));
                    t.rate_(1.0.rand2);
                    0.8.wait;
            }
        }
};
)

(
fork{
    1.do{|i|
            16.do{|i|
                    t.buf_([~b1, ~b2, ~b3].wrapAt(i));
                    t.rate_(1.0.rand2);
                    1.0.wait;
            };
        5.wait;
        5.do{|i|
                    t.buf_([~b1, ~b2, ~b3].wrapAt(i));
                    t.rate_(0.3.rand2);
                    0.2.wait;
            }
        }
};
)

//now for something completely different
(
t.rate_(1.7);
t.pos_({0.2.rrand(0.9)});
t.dur_({0.01.rrand(0.1)});
t.delta_({0.2.rrand(3.3)});    //delta can be a function
)

(
t.rate_(1.2);
t.pos_({0.1.rrand(0.3)});
t.dur_({0.3.rrand(0.8)});
t.delta_({0.7.rrand(2.3)});    //delta can be a function
)

t.pause
t.start

//and all of the controllers can be functions
(
t.buf_({[~b1, ~b2, ~b3].choose});
t.rate_({8.rand2});
t.pos_({0.3.rand});
t.dur_({0.05.rrand(0.35)});
t.pan_({1.0.rand2});
t.amp_({0.5.rrand(1.0)});
t.delta_({0.01.rrand(0.8)});
)

(
t.buf_({[~b1, ~b2, ~b3].choose});
t.rate_({2.rand2});
t.pos_({0.9.rand});
t.dur_({0.5.rrand(0.95)});
t.pan_({1.0.rand2});
t.amp_({0.5.rrand(1.0)});
t.delta_({0.1.rrand(0.4)});
)

//quantise playbackrate
t.rate_({10.rand.round(5)});
t.rate_({10.rand.round(4)});
t.rate_({10.rand.round(3)});

//back to static amp and deltatime
t.amp_(0.3);
t.delta_(0.005);    //default
t.dur_(0.1);
t.buf_(~b1);
t.pos_(0);

//take out more and more and stop
(
fork{
    30.do{|i| r.delta_(r.delta*1.1); 0.2.wait};
    r.stop;
};
)

//clean up
~b1.free;
~b2.free;
~b3.free;
