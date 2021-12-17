#!/usr/bin/env python3

import numpy as np
import copy
import itertools

map0 = """\
[({(<(())[]>[[{[]{<()<>>
[(()[<>])]({[<{<<[]>>(
{([(<{}[<>[]}>{[]{[(<()>
(((({<>}<{<{<>}{[]{[]{}
[[<[([]))<([[{}[[()]]]
[{[{({}]{}}([{[{{{}}([]
{<[[]]>}<{[{[{[]{()[[[]
[<(<(<(<{}))><([]([]()
<{([([[(<>()){}]>(<<{{
<{([{{}}[<[[[<>{}]]]>[]]"""

opening = ['{', '(', '[', '<']
closing = ['}', ')', ']', '>']
oc = [('{','}'), ('(',')'), ('[', ']'), ('<', '>')]
oc_map = dict(oc)
co_map = dict([(c,o) for (o,c) in oc])
score_map = dict([(None, 0), ('}', 1197), (')', 3), (']', 57), ('>', 25137)])
score_map2 = dict([('}', 3), (')', 1), (']', 2), ('>', 4)])

def check(line, pending):
    if len(line) == 0:
        return (None, pending)
    ch = line[0]
    if ch in opening:
        return check(line[1:], [oc_map[ch]] + pending)
    else:
        if (pending[0] == ch):
            return check(line[1:], pending[1:])
        else:
            return (ch, pending)

def part0():
    lines = map0.split('\n')
    res = 0
    for line in lines:
        c = check(line, [])[0]
        print(line, c, score_map[c])
        res += score_map[c]
    assert 26397 == res

def part1():
    lines = [line.strip() for line in open('input10.txt')]
    res = 0
    for line in lines:
        c = check(line, [])[0]
        print(line, c, score_map[c])
        res += score_map[c]
    print(res)
    assert 392421 == res

def part2_0():
    lines = map0.split('\n')
    res = []
    for line in lines:
        (fault, pending) = check(line, [])
        if fault:
            continue
        print(line, pending)
        sc = 0
        for p in pending:
            sc *= 5
            sc += score_map2[p]
        res.append(sc)
    print(res)
    res.sort()
    res = res[int(len(res)/2)]
    print(res)
    assert 288957 == res

def part2():
    lines = [line.strip() for line in open('input10.txt')]
    res = []
    for line in lines:
        (fault, pending) = check(line, [])
        if fault:
            continue
        print(line, pending)
        sc = 0
        for p in pending:
            sc *= 5
            sc += score_map2[p]
        res.append(sc)
    print(res)
    res.sort()
    res = res[int(len(res)/2)]
    print(res)
    assert 2769449099 == res

if __name__ == '__main__':
    part0()
    part1()
    part2_0()
    part2()
