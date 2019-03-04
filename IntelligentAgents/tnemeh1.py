'''Critter that learns optimal behavior based on probabilities that shift with negative and positive reinforcement'''
import critter
import color 
import random

class Tnemeh1(critter.Critter):
    #possible for critter to choose from when moving
    directions=[critter.NORTH, critter.SOUTH, critter.WEST, critter.EAST]

    def __init__(self):
        super().__init__()
        
        #initial probabilites for each strategy
        self.fight = .33
        self.party = .33
        self.heal = .33

        #strategy chosen in interaction
        self.strategy = None
        
        #number that determines the degree to which probabilities shift based on positive/negative reinforcement
        self.scale = .1
        
    # @param oppInfo The critter info of the current opponent.
    # @returns Your action: ROAR, POUNCE, SCRATCH, PARTY, or HEAL
    def interact(self, oppInfo):
        #pick a strategy based on probabilities
        rand = random.uniform(0, 1)
        if rand <= self.fight:
            self.strategy="fight"
            return self.pickStrategy()
        elif rand > self.fight and rand <= self.fight + self.party:
            self.strategy="party"
            return critter.PARTY
        else:
            #if other critter has 100 health, pick another strategy
            while self.strategy is "heal" and oppInfo.getNeighborHealth(critter.CENTER) is 100:
                rand = random.uniform(0, 1)
                if rand <= self.fight:
                    self.strategy="fight"
                    return self.pickStrategy()
                elif rand > self.fight and rand <= self.fight + self.party:
                    self.strategy="party"
                return critter.PARTY
            return critter.HEAL

    # Pick 1 of 3 fighting strategies with equal probability
    # @returns fighting strategy 
    def pickStrategy(self):
        strategies = [critter.ROAR, critter.POUNCE, critter.SCRATCH]
        return random.choice(strategies)
    
    # Give your color.
    # @returns Your current color.
    def getColor(self):
        return color.BLUE
    
    # Give your direction. Move to interact with other critter if possible
    # @param info your critter info
    # @returns A cardinal direction, in the form of a constant (NORTH, SOUTH)
    def getMove(self, info):
        for move in Tnemeh1.directions:
            if info.getNeighbor(move) is not ".":
                return move

        return random.choice(Tnemeh1.directions)
    
    # Give your character.
    # @returns Whichever character represents this critter.
    def getChar(self):
        return '1'
    
    # End of interaction shenanigans. Probabilities of future actions shift based on outcome.
    # @param won Boolean; true if won the interaction, false otherwise.
    # @param oppFight Opponent's choide of fight strategy (ROAR, etc)
    # @returns Nothing.
    def interactionOver(self, won, oppFight):
        # point system where 1 karma/10 health/enemy loosing 10 health = 1 unit
        points = 0

        #calculate number of points based on result
        if oppFight is critter.ROAR or oppFight is critter.POUNCE or oppFight is critter.SCRATCH:
            if won is True:
                points += 1 + 2.5
            else:
                points -= 2.5
        else:
            if self.strategy is "fight":
                if oppFight is critter.PARTY:
                    points = points - 3 + 2.5
                else:
                    points = points - 5 + 2.5
            elif self.strategy is "party":
                points += 3
            else:
                points += 5

        #probabilities shift based on number of points and scale factor
        shift = self.scale * points
        if self.strategy is "fight":
            self.fight += shift
            self.heal -= shift/2
            self.party -= shift/2
        elif self.strategy is "heal":
            self.fight -= shift/2
            self.heal += shift
            self.party -= shift/2
        elif self.strategy is "party":
            self.fight -= shift/2
            self.heal -= shift/2
            self.party += shift

        #scale factor decreases over time, as strategy is honed and gets better
        if self.scale > .02:
            self.scale -= .005


