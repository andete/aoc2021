#!/usr/bin/env python3

import numpy as np
import copy

def tick(fish):
    if fish == 0:
        fish = 6
        return (fish, 8)
    else:
        fish -= 1
        return (fish, None)

def state_tick(state):
    new_fishes = []
    for i in range(len(state)):
        (updated_fish, new_fish) = tick(state[i])
        state[i] = updated_fish
        if new_fish:
            new_fishes.append(new_fish)
    state += new_fishes

def part0():
    state = [3,4,3,1,2]
    for _ in range(18):
        state_tick(state)
    print(len(state), state)
    assert 26 == len(state)
    for _ in range(80 - 18):
        state_tick(state)
    print(len(state))
    assert 5934 == len(state)


def part1():
    state = [int(x) for x in open('input6.txt').read().split(',')]
    for _ in range(80):
        state_tick(state)
    print(len(state))

def part2():
    #state = [3,4,3,1,2]
    state = [int(x) for x in open('input6.txt').read().split(',')]
    state2 = [0, 0, 0, 0, 0, 0, 0, 0, 0]
    for x in state:
        state2[x] += 1
    print(state, state2)
    for _ in range(256):
        #         0          1          2          3          4          5          6                      7          8
        state2 = [state2[1], state2[2], state2[3], state2[4], state2[5], state2[6], state2[7] + state2[0], state2[8], state2[0]]
    print(state2, sum(state2))

if __name__ == '__main__':
    part0()
    part1()
    part2()
