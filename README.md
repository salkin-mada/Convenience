# Convenience for Verdensteatret's Max 8 Package (VTMax)
Convenience is many things but first and foremost it is convenient for me.

But if you like to **ease** - read on.

This branch is a special VTMax version utilizing the Busplug Extension methods for SpatProxy's!

Very convenient indeed.

![example](images/crawling_trees.png "tree'n crawl")

Convenience is a tool to easily load audio files into easily accessible collections of buffers in Supercollider. 

It is a tool to easily play these buffers.

It is a great tool for discovering surprising audio file mixtures.

Convenience has a crawler method that can recursively go through a directory tree. And load buffers catalogued together in keys related to their placement in the tree.

It can sequence between folders and files, while controlling playback position, pitch/rate, amplitude and pitch envelopes, fft-filtering and more.
Several Convenience sequencer/pattern instances can run side by side, as independent tracks.
It is Pattern Class based so you can do your usual pattern grub but with obvious limitations in regard to the more demanding Pattern Class usages.

It can load and remove buffer groups (folders of audio files) while playing.

Convenience is a good friend of nvme disks and lots of ram.

This Class is a work in progress. It is unstable. It can change in ways where saved usage of the Class does not function any more.

## Convenience is
at ease, comfortable, leisurely, spontaneous, free and easy, loose, forthright, calm, peaceful, tranquil, careless, carefree, untroubled, relaxed, equable, hospitable, on easy street.

simple, facile, effortless, obvious, apparent, clear, uncomplicated, elementary, yielding, easily done, not burdensome, requiring not so much effort, presenting few difficulties, smooth, manageable, accessible, wieldy, straightforward, semi foolproof, slight, mere, light, undemanding,

It is plain sailing and easy as pie

## Installation

In SuperCollider, evaluate the following code to install it as a quark: 

```scd
Quarks.install("https://github.com/salkin-mada/Convenience.git")
```

## Usage

`Convenience.crawl`

Opens a window where you can drag and drop folders + control "loading depth" i.e. recursive steps into/along a directory tree.

`Convenience.crawl(path, depth)`

start crawling immediately from given path into its tree

Both creates a dict of `Buffer`'s loaded with the audio files from the folders found in the given directory tree (path). 
(please note that for now Convenience only creates mono buffers)

Each folder's contents are accessible as an array of buffers at a dict key based on the folder name formatted into a usable key (name/group id).

`Convenience.p(\name)`

create and start a convenient sequencer, giving it a name is the minimum requirement

`Convenience.properties`

list all arguments available in the .p method

`Convenience.get(folder, index)`

Returns the `Buffer` of file at index in chosen folder - if queried index is to high it will wrap around the size of files in the chosen folder until it finds an existing index. Also if chosen folder does not exist it will default to first one in the dictionary of loaded folders.
No need to worry about asking for a file number or folder key which is not there.
This is true for both the `.get` and `.p` method.

`ZzzZzZzzzZz` can be used as a "left-hander" instead of the "two-hander" Convenience. :)

## Example

```scd
s.boot;

z = Convenience;

/* crawler loads folders in its way */
z.crawl

// no gui usage
z.crawl(Platform.resourceDir +/+ "sounds", depth: 0)

z.list // get some info of what went down

//minimum
z.p(\lay, '/myScene/spatProxy.1'); // play at the beginning of first file in first folder at the allocated outputs given from spatProxy (allocator)
z.s(\lay); // stop

// play
z.p(
    \lay,
    '/myScene/spatProxy.1',
    folder: \sounds,
    index: Prand([1,2,3], inf),
    pos: Pwhite(0.2,0.7),
    dur: Pseq([1,3,Rest(2),2], inf),
    rate: Pwhite(0.9,1.0) * Pstep([0.4,0.9,0.2], Prand([3,5,8], inf), inf),
    pan: Pwhite(-0.65,0.65),
    amp: 0.4
);

// slay
z.s(\lay)

z.sall // stop all Convenience patterns

// play pattern and create spatProxy connection
z.p(\spat1, '/myScene/spatProxy.1', index: 15, dur: 0.5, pos: Psinen(0.1))
z.p(\spat2, '/myScene/spatProxy.2', index: 15, dur: 0.65, pos: Psinen(0.1), rate: 2.0)
z.p(\spat3, '/myScene/spatProxy.3', index: 3, dur: 0.65, pos: Psinen(0.1), rate: 1.0)
z.p(\spat4, '/glemsel/yo.1', index: 22, dur: 0.65, pos: Psinen(0.1), rate: 1.0)
z.p(\spat5, '/hurski/cirkus.3', index: 17, dur: 2.35, pos: Psinen(0.1), rate: 1.0)
z.p(\spat6, '/orange/friskModd.42', index: 12, dur: 0.95, pos: Psinen(0.1), rate: 1.0/2)

// stop pattern
z.s(\spat1);
z.s(\spat2);
z.s(\spat3);
z.s(\spat4);
z.s(\spat5);
z.s(\spat6);

( // remove spatProxy connection
z.ss(\spat1, '/myScene/spatProxy.1');
z.ss(\spat2, '/myScene/spatProxy.2');
z.ss(\spat3, '/myScene/spatProxy.3');
z.ss(\spat4, '/glemsel/yo.1');
z.ss(\spat5, '/hurski/cirkus.3');
z.ss(\spat6, '/orange/friskModd.42');
)
```
Convenience.p outputs stereo, 2 channels, but can be sequenced to whatever output pair you like.
For more info about `.p` arguments check the `convenience_guide.scd` in /Convenience/usage or simply check the helpfile, `ctrl+d` / `cmd+d` depending on your OS.

