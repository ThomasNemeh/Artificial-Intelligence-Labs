'''critter species that tries to kill 10 other critters from other speicies before trying to cluster with itself'''
import critter
import color 
import random

class Tnemeh4(critter.Critter):
    
    fightStrategies = [critter.ROAR, critter.POUNCE, critter.SCRATCH]
    directions=[critter.NORTH, critter.SOUTH, critter.WEST, critter.EAST]

    def __init__(self):
        super().__init__()
        self.fightStrategy = None
        self.enemiesKilled = 0
        self.seperate = 0
        self.clusterIterations = 0
        
    # @param oppInfo The critter info of the current opponent.
    # @returns Your action: ROAR, POUNCE, SCRATCH, PARTY, or HEAL
    def interact(self, oppInfo):
        #heal/party with same species
        if oppInfo.getNeighbor(critter.CENTER) is "Tnemeh4":
            self.clusterIterations = self.clusterIterations + 1
            if oppInfo.getNeighborHealth(critter.CENTER) < 100:
                return critter.HEAL
            else:
                return critter.PARTY
        #attack other species
        else:
            self.fightStrategy = random.choice(Tnemeh4.fightStrategies)
            return self.fightStrategy
    
    # Give your color.
    # @returns Your current color.
    def getColor(self):
        return color.BLACK
    
    # Give your direction.
    # @param info your critter info
    # @returns A cardinal direction, in the form of a constant (NORTH, SOUTH)
    def getMove(self, info):
        if self.enemiesKilled < 10:
            #first preference is to move towards same species, unless we have been clustered for more than 3 iterations
            if self.seperate > 1:
                self.seperate = 0
                self.clusterIterations = 0
                for move in Tnemeh4.directions:
                    if info.getNeighbor(move) is not "Tnemeh4":
                        return move

                return random.choice(Tnemeh4.directions)
            else:
                for move in Tnemeh4.directions:
                    if info.getNeighbor(move) is "Tnemeh4":
                        return move

                for move in Tnemeh4.directions:
                    if info.getNeighbor(move) is not ".":
                        return move

                return random.choice(Tnemeh4.directions)
        else:
            #if we have already killed 10 other critters, try to avoid conflict with other species and cluster with same species
            hostileNeighbors = []
            for move in Tnemeh4.directions:
                if info.getNeighbor(move) is "Tnemeh4":
                    return move
                elif info.getNeighbor(move) is not ".":
                    hostileNeighbors.append(move)
            options = [x for x in Tnemeh4.directions if x not in hostileNeighbors]
            if len(options) > 0:
                return random.choice(options)
            else:
                return random.choice(Tnemeh4.directions)

                    
    
    # Give your character.
    # @returns Whichever character represents this critter.
    def getChar(self):
        return '4'
    
    # End of interaction shenanigans.
    # @param won Boolean; true if won the interaction, false otherwise.
    # @param oppFight Opponent's choide of fight strategy (ROAR, etc)
    # @returns Nothing.
    def interactionOver(self, won, oppFight):
        # seperate if we have been clustered wth same species for more than 3 iterations
        if self.clusterIterations >= 4:
            self.seperate += 1

        #record most successful fight strategy is memory
        if oppFight is critter.ROAR or oppFight is critter.POUNCE or oppFight is critter.SCRATCH:
            if won is True:
                Tnemeh4.fightStrategies.append(self.fightStrategy)
                self.enemiesKilled += 1
            else:
                if Tnemeh4.fightStrategies.count(self.fightStrategy > 1):
                    Tnemeh4.fightStrategies.remove(self.fightStrategy)
        


