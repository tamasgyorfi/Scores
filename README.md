# Scores
Service for evaluating user bets given a match's final result. 

## Payload format for the results endpoint
{  
	"matchResult":{  
        "result":{  
            "matchId":""+matchId+"",  
            "competitionId":"compId100",  
            "homeTeamId":"team1",  
            "awayTeamId":"team2",  
            "homeTeamGoals":1,  
            "awayTeamGoals":1  
        }  
    },  
    "securityToken":"ashdj"  
}  
