package io.github.marcelofabianov.redweave.domain.network;

/** One slot of one node, the key of the last reading of an input. */
record SlotRef(NodeId node, int index) {}
