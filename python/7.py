#!/usr/bin/env python3

import numpy as np
import copy

def part0():
    state = [16,1,2,0,4,2,7,1,2,14]
    min_fuel = 1000000
    pos = -1
    for i in range(min(state), max(state)+1):
        fuel = sum([abs(x - i) for x in state])
        if fuel < min_fuel:
            min_fuel = fuel
            pos = i
    print(pos, min_fuel)
    assert 2 == pos
    assert 37 == min_fuel

def part0_2():
    state = [16,1,2,0,4,2,7,1,2,14]
    min_fuel = 1000000
    pos = -1
    for i in range(min(state), max(state)+1):
        fuel = sum([sum(range(1, abs(x - i)+1)) for x in state])
        if fuel < min_fuel:
            min_fuel = fuel
            pos = i
    print(pos, min_fuel)
    assert 5 == pos
    assert 168 == min_fuel


def part1():
    state = [int(i) for i in open('input7.txt').read().split(',')]
    min_fuel = 1000000
    pos = -1
    for i in range(min(state), max(state)+1):
        fuel = sum([abs(x - i) for x in state])
        if fuel < min_fuel:
            min_fuel = fuel
            pos = i
    print(pos, min_fuel)

def part2():
    state = [int(i) for i in open('input7.txt').read().split(',')]
    min_fuel = 1000000000
    pos = -1
    for i in range(min(state), max(state)+1):
        fuel = sum([sum(range(1, abs(x - i)+1)) for x in state])
        if fuel < min_fuel:
            min_fuel = fuel
            pos = i
    print(pos, min_fuel)

if __name__ == '__main__':
    part0()
    part1()
    part0_2()
    part2()
