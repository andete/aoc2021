#!/usr/bin/env python

import copy

small = """00100
11110
10110
10111
10101
01111
00111
11100
10000
11001
00010
01010"""

def part0():
    bitslist = [[int(bit) for bit in bits.strip()] for bits in small.split()]
    numbits = len(bitslist[0])
    gammabits = []
    epsilonbits = []
    for i in range(0, numbits):
        bits = [bits[i] for bits in bitslist]
        zeros = bits.count(0)
        ones = bits.count(1)
        gamma = 0
        epsilon = 0
        if (ones > zeros):
            gamma = 1
        if (ones <= zeros):
            epsilon = 1
        gammabits.append(gamma)
        epsilonbits.append(epsilon)
    gamma = int("".join([str(i) for i in gammabits]), 2)
    epsilon = int("".join([str(i) for i in epsilonbits]), 2)
    print(gamma, epsilon, gamma * epsilon)

def part1():
    bitslist = [[int(bit) for bit in bits.strip()] for bits in open('input3.txt')]
    numbits = len(bitslist[0])
    gammabits = []
    epsilonbits = []
    for i in range(0, numbits):
        bits = [bits[i] for bits in bitslist]
        zeros = bits.count(0)
        ones = bits.count(1)
        gamma = 0
        epsilon = 0
        if (ones > zeros):
            gamma = 1
        if (ones < zeros):
            epsilon = 1
        gammabits.append(gamma)
        epsilonbits.append(epsilon)
    gamma = int("".join([str(i) for i in gammabits]), 2)
    epsilon = int("".join([str(i) for i in epsilonbits]), 2)
    print(gamma, epsilon, gamma * epsilon)


def part2():
    bitslist = [[int(bit) for bit in bits.strip()] for bits in open('input3.txt')]
    numbits = len(bitslist[0])
    oxigenbits = []
    scrubbits = []
    oxigeninputbits = copy.deepcopy(bitslist)
    scrubinputbits = copy.deepcopy(bitslist)

    for i in range(0, numbits):
        bits = [bits[i] for bits in oxigeninputbits]
        zeros = bits.count(0)
        ones = bits.count(1)
        ox = 1
        if (ones < zeros):
            ox = 0
        oxigeninputbits = [x for x in oxigeninputbits if x[i] == ox]
        if len(oxigeninputbits) == 1:
            oxigenbits = oxigeninputbits[0]
            break

    for i in range(0, numbits):
        bits = [bits[i] for bits in scrubinputbits]
        zeros = bits.count(0)
        ones = bits.count(1)
        sc = 0
        if (ones < zeros):
            sc = 1
        scrubbits.append(sc)
        scrubinputbits = [x for x in scrubinputbits if x[i] == sc]
        if len(scrubinputbits) == 1:
            scrubbits = scrubinputbits[0]
            break

    oxigen = int("".join([str(i) for i in oxigenbits]), 2)
    scrub = int("".join([str(i) for i in scrubbits]), 2)
    print(oxigen, scrub, oxigen * scrub)



if __name__ == '__main__':
    part0()
    part1()
    part2()
