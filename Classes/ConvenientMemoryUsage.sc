+ Buffer {
    memoryUsage {
        // in bytes
        ^numFrames * numChannels * 4
    }
}

+ Convenience {

    *memoryUsage {
        ^buffers.sum { | array | array.sum { | buffer | buffer.memoryUsage.asFloat } }
    }

    *memoryUsageMb {
        // ^this.memoryUsage div: 1e6
        // ^this.memoryUsage / 1e6
        ^((this.memoryUsage/1e6).round.asInteger)
    }
}
