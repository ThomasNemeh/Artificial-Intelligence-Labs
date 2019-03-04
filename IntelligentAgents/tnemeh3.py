'''Critter that tries to cluster with own species and heal/party, while attacking all other species'''
import critter
import color 
import random

class Tnemeh3(critter.Critter):
    #memory of successful fight strategies maintained with list
    fightStrategies = [critter.ROAR, critter.POUNCE, critter.SCRATCH]
    directions=[critter.NORTH, critter.SOUTH, critter.WEST, critter.EAST]

    def __init__(self):
        super().__init__()
        self.fightStrategy = None
        self.inCluster = False
        
    # @param oppInfo The critter info of the current opponent.
    # @returns Your action: ROAR, POUNCE, SCRATCH, PARTY, or HEAL
    def interact(self, oppInfo):
        # Heal same species if health < 100, else party
        if oppInfo.getNeighbor(critter.CENTER) is "Tnemeh3":
            self.inCluster = True
            if oppInfo.getNeighborHealth(critter.CENTER) < 100:
                return critter.HEAL
            else:
                return critter.PARTY
        else:
            self.fightStrategy = random.choice(Tnemeh3.fightStrategies)
            return self.fightStrategy
    
    # Give your color.
    # @returns Your current color.
    def getColor(self):
        return color.RED
    
    # Give your direction.
    # @param info your critter info
    # @returns A cardinal direction, in the form of a constant (NORTH, SOUTH)
    def getMove(self, info):
        #move with first preference towards same speicies
        if self.inCluster is True:
            for move in Tnemeh3.directions:
                if info.getNeighbor(move) is "Tnemeh3":
                    return move
        else:
            #second preference is to move to attack other species
            for move in Tnemeh3.directions:
                if info.getNeighbor(move) is not ".":
                    return move
        #move randomly if there are no critters in the vicinity
        return random.choice(Tnemeh3.directions)
    
    # Give your character.
    # @returns Whichever character represents this critter.
    def getChar(self):
        return '3'
    
    # End of interaction shenanigans.
    # @param won Boolean; true if won the interaction, false otherwise.
    # @param oppFight Opponent's choide of fight strategy (ROAR, etc)
    # @returns Nothing.
    def interactionOver(self, won, oppFight):
        # add successful fight strategy to memory, while removing unsuccessful fight strategy
        if oppFight is critter.ROAR or oppFight is critter.POUNCE or oppFight is critter.SCRATCH:
            if won is True:
                Tnemeh3.fightStrategies.append(self.fightStrategy)
            else:
                #ensure that there is one instance of each fight strategy in memory
                if Tnemeh3.fightStrategies.count(self.fightStrategy > 1):
                    Tnemeh3.fightStrategies.remove(self.fightStrategy)
        


