-- module (NICHT ÄNDERN!)
module XiangqiBot
    ( getMove
    , listMoves
    ) 
    where

import Data.Char as C
-- More modules may be imported

import Util
data Position = Position {getX :: Int, getY :: Int} deriving Eq


data Move = Move {initialPos :: Position, finalPos :: Position, name :: Char}

data Figure = Figure {pos :: Position, val :: Char}
--- external signatures (NICHT ÄNDERN!)

getMove :: String -> String
getMove s = listMoves s

listMoves :: String -> String
listMoves fen = let
                    figList = getFigures fen 
                    dir  = getDirection fen 
                    moveList = preListMoves figList dir
                    result = filter (\m -> not ( (isSchach figList m (-dir)) || (isTodesblick figList m dir) ) ) moveList
               in
                    "[" ++ (init (moveToString result)) ++ "]"

-- YOUR IMPLEMENTATION FOLLOWS HERE

getFigures :: String -> [Figure]
getFigures fen = h 0 0 (init (init fen)) []
            where 
            h :: Int -> Int -> String -> [Figure] -> [Figure]
            h _ _ [] l = l
            h x y s l = if head s == '/' then 
                            h 0 (y+1) (tail s) l
                        else
                           if (C.isAlpha (head s)) == True then   
                                h (x+1) y (tail s) (l ++ [Figure (Position x y) (head s) ])
                           else 
                                h (x + (C.digitToInt (head s))) y (tail s) l

getDirection :: String -> Int
getDirection s = if last s == 'b' then 1
                 else -1


 
inBoard :: [Position]->[Position] -> [Position]
inBoard pList mypList = filter (\p -> (not (elem p mypList)) && ((getX p) > -1) && ((getX p) < 9) && ((getY p) > -1) && ((getY p) < 10)) pList 

inPalace :: [Position]->[Position] -> [Position]
inPalace pList mypList = filter (\p -> 
                                   (not (elem p mypList)) && 
                                   (((getX p) > 2) && ((getX p) < 6)) && 
                                   ((((getY p) > 6) && ((getY p) < 10)) || (((getY p) > -1) && ((getY p) < 3)))
                                )
                                   
                                pList

inArea :: [Position] -> [Position] ->Int-> [Position]--for Elephant after inBoard
inArea pList mypList dir = filter (\p -> 
                                 (not (elem p mypList)) &&
                                (if dir == 1 then
                                   if (((getY p) > -1) && ((getY p) < 5)) then True 
                                   else False
                                 else
                                   if (((getY p) > 4) && ((getY p) < 10)) then True
                                   else False )
                         )
                         pList


getSoldierMoves :: Position -> Int -> [Position] -- int is direction, gives possible final positions which needs to be filtered
getSoldierMoves p n = let 
                        y = getY p
                        x = getX p
                        in
                        if n == 1 then -- black
                              if y > 4 then
                                   (Position (x+1) y) : (Position (x-1) y) : (Position x (y+1)) : []
                              else  
                                   (Position x (y+1)) : []
                         else
                             if y < 5 then
                                   (Position (x+1) y) : (Position (x-1) y) : (Position x (y-1)) : []
                              else  
                                   (Position x (y-1)) : []

getGeneralMoves ::  Position -> [Position] 
getGeneralMoves p = let
                         y = getY p
                         x = getX p
                         in 
                         (Position (x+1) y) : (Position (x-1) y) : (Position x (y+1)) : (Position x (y-1)) : []

getAdvisorMoves :: Position -> [Position]
getAdvisorMoves p = let
                         y = getY p
                         x = getX p
                         in 
                         (Position (x+1) (y+1)) : (Position (x+1) (y-1)) : (Position (x-1) (y+1)) : (Position (x-1) (y-1)) : []


getElephantMoves :: [Figure] -> Position -> [Position]  --2)Path checked, 1) occupied figure is of player figuers
getElephantMoves f p = let
                         y = getY p
                         x = getX p
                         l = filter (\x -> x /= p ) (map (\x -> pos x) f)
                         in 
                         h l x y 0 
                         where
                         h ::[Position] -> Int -> Int -> Int -> [Position]
                         h l x y 0 = if elem (Position (x+1) (y+1)) l then h l x y 1 
                                     else  [(Position (x+2) (y+2))] ++ h l x y 1

                         h l x y 1 = if elem (Position (x+1) (y-1)) l then h l x y 2 
                                     else  [(Position (x+2) (y-2))] ++ h l x y 2

                         h l x y 2 = if elem (Position (x-1) (y+1)) l then h l x y 3
                                     else  [(Position (x-2) (y+2))] ++ h l x y 3

                         h l x y 3 = if elem (Position (x-1) (y-1)) l then [] 
                                     else  [(Position (x-2) (y-2))]


getHorseMoves :: [Figure] -> Position -> [Position] --1) 2)Path checked, figure is of player figuers
getHorseMoves f p = let
                         y = getY p
                         x = getX p
                         l = filter (\x -> x /= p ) (map (\x -> pos x) f)
                         in 
                         (h l x y 0)
                         where
                         h :: [Position] -> Int -> Int  -> Int -> [Position]
                         h l x y 0 = if elem (Position (x+1) (y+1)) l then h l x y 1 
                                     else  ((Position (x+1) (y+2)) : (Position (x+2) (y+1)) :[]) ++ h l x y 1

                         h l x y 1 = if elem (Position (x+1) (y-1)) l then h l x y 2 
                                     else  ((Position (x+1) (y-2)) : (Position (x+2) (y-1)) :[]) ++ h l x y 2

                         h l x y 2 = if elem (Position (x-1) (y+1)) l then h l x y 3
                                     else  ((Position (x-1) (y+2)) : (Position (x-2) (y+1)) :[]) ++ h l x y 3

                         h l x y 3 = if elem (Position (x-1) (y-1)) l then [] 
                                     else  ((Position (x-1) (y-2)) : (Position (x-2) (y-1)) :[])  
                         



getRookMoves :: [Figure] -> Position -> Int -> [Position]-- list of all figures,0) 1)2) ,int is direction
getRookMoves f p n = let    
                         l = map (\x -> pos x) f 
                         myL = map (\x -> pos x)
                              (filter 
                                   (\x -> (if (n == 1) then
                                             if C.isLower (val x) then True 
                                        else  False
                                          else
                                             if C.isUpper (val x) then True 
                                        else False)
                              ) f)
                         in 
                         h p l myL 0 (getX p)
                         where
                         h :: Position -> [Position]->[Position] -> Int -> Int -> [Position]
                         h p l myL 0 a = let x = (a + 1) --a is x
                                             y = getY p
                                             in
                                             if (x < 9) then 
                                                  if elem (Position x y ) l then
                                                       if elem (Position x y) myL then h p l myL 1 (getX p)
                                                       else ((Position x y):[]) ++ (h p l myL 1 (getX p))
                                                  else
                                                     ((Position x y):[]) ++ (h p l myL 0 x) 
                                             else
                                                  h p l myL 1 (getX p)


                         h p l myL 1 a = let x = (a - 1) 
                                             y = getY p
                                             in
                                             if (x > -1 ) then 
                                                  if elem (Position x y ) l then
                                                       if elem (Position x y) myL then h p l myL 2 (getY p)
                                                       else ((Position x y):[]) ++ (h p l myL 2 (getY p))
                                                  else
                                                     ((Position x y):[]) ++ (h p l myL 1 x) 
                                             else
                                                  h p l myL 2 (getY p)--a is x-


                         h p l myL 2 a = let x = getX p --a is y+
                                             y = a + 1
                                             in
                                             if (y < 10 ) then 
                                                  if elem (Position x y ) l then
                                                       if elem (Position x y) myL then h p l myL 3 (getY p)
                                                       else ((Position x y):[]) ++ (h p l myL 3 (getY p))
                                                  else
                                                     ((Position x y):[]) ++ (h p l myL 2 y) 
                                             else
                                                  h p l myL 3 (getY p)
                         h p l myL 3 a = let x = getX p --a is y-
                                             y = a - 1
                                             in
                                             if (y > -1 ) then 
                                                  if elem (Position x y ) l then
                                                       if elem (Position x y) myL then []
                                                       else ((Position x y):[])
                                                  else
                                                     ((Position x y):[]) ++ (h p l myL 3 y) 
                                             else
                                                  []

getCannonMoves :: [Figure] -> Position -> Int -> [Position]-- list of all figures,0) 1)2) ,int is direction
getCannonMoves f p n = let    
                         l = map (\x -> pos x) f 
                         myL = map (\x -> pos x)
                              (filter 
                                   (\x -> (if (n == 1) then
                                             if C.isLower (val x) then True 
                                        else  False
                                          else
                                             if C.isUpper (val x) then True 
                                        else False)
                              ) f)
                         in 
                         h p l myL 0 (getX p) False
                         where
                         h :: Position -> [Position]->[Position] -> Int -> Int -> Bool -> [Position]
                         h p l myL 0 a attack = let    x = (a + 1) --a is x
                                                       y = getY p
                                                       in
                                                       if (x < 9) then 
                                                            if attack then 
                                                                 if elem (Position x y ) l then
                                                                      if elem (Position x y) myL then h p l myL 1 (getX p) False 
                                                                      else ((Position x y):[]) ++ h p l myL 1 (getX p) False
                                                                 else
                                                                      h p l myL 0 x True
                                                            else
                                                                 if elem (Position x y ) l then
                                                                      h p l myL 0 x True 
                                                                 else
                                                                      ((Position x y):[]) ++ h p l myL 0 x False
                                                       else
                                                            h p l myL 1 (getX p) False

                         h p l myL 1 a attack = let    x = (a - 1) --a is x
                                                       y = getY p
                                                       in
                                                       if (x > -1) then 
                                                            if attack then 
                                                                 if elem (Position x y ) l then
                                                                      if elem (Position x y) myL then h p l myL 2 (getY p) False 
                                                                      else ((Position x y):[]) ++ h p l myL 2 (getY p) False
                                                                 else
                                                                      h p l myL 1 x True
                                                            else
                                                                 if elem (Position x y ) l then
                                                                      h p l myL 1 x True 
                                                                 else
                                                                      ((Position x y):[]) ++ h p l myL 1 x False
                                                       else
                                                            h p l myL 2 (getY p) False

                         h p l myL 2 a attack = let    y = (a + 1) --a is x
                                                       x = getX p
                                                       in
                                                       if (y < 10) then 
                                                            if attack then 
                                                                 if elem (Position x y ) l then
                                                                      if elem (Position x y) myL then h p l myL 3 (getY p) False 
                                                                      else ((Position x y):[]) ++ h p l myL 3 (getY p) False
                                                                 else
                                                                      h p l myL 2 y True
                                                            else
                                                                 if elem (Position x y ) l then
                                                                      h p l myL 2 y True 
                                                                 else
                                                                      ((Position x y):[]) ++ h p l myL 2 y False
                                                       else
                                                            h p l myL 3 (getY p) False

                         h p l myL 3 a attack = let    y = (a - 1) --a is x
                                                       x = getX p
                                                       in
                                                       if (y > -1) then 
                                                            if attack then 
                                                                 if elem (Position x y ) l then
                                                                      if elem (Position x y) myL then []
                                                                      else ((Position x y):[]) 
                                                                 else
                                                                      h p l myL 3 y True
                                                            else
                                                                 if elem (Position x y ) l then
                                                                      h p l myL 3 y True 
                                                                 else
                                                                      ((Position x y):[]) ++ h p l myL 3 y False
                                                       else
                                                            []                                                         
toChar :: Int -> Char 
toChar 0 = 'a'
toChar 1 = 'b'
toChar 2 = 'c'
toChar 3 = 'd'
toChar 4 = 'e'
toChar 5 = 'f'
toChar 6 = 'g'
toChar 7 = 'h'
toChar 8 = 'i'



createMoves :: Position -> Char -> [Position]  -> [Move]
createMoves _ _ [] = []
createMoves p c (x:xs) = [(Move p x c)] 
                       ++ (createMoves p c xs) 
                      
moveToString :: [Move] ->  String 
moveToString [] = ""
moveToString (x:xs) = ((toChar (getX (initialPos x)))
                         :(C.intToDigit (9 - (getY (initialPos x))))
                         :'-'
                         :(toChar (getX (finalPos x)))
                         :(C.intToDigit (9 - (getY (finalPos x))))
                         :','
                         :[])
                         ++ (moveToString xs)  

--listMoves ergibt eine Liste von Spielzuege als string



preListMoves :: [Figure] -> Int -> [Move]
preListMoves fList dir = let
               myL = 
                    filter 
                         (\x -> (if (dir == 1) then
                                   if C.isLower (val x) then True 
                                   else  False
                                 else
                                   if C.isUpper (val x) then True 
                                   else False))
                         fList
               myPos = map (\x -> pos x) myL
               in
                    h fList myL  myPos dir
               where
                    h :: [Figure] -> [Figure] -> [Position] -> Int -> [Move] 
                    h _ [] _ _ = []
                    h fList (m : ms) mypList dir  = if ((val m == 's') || (val m == 'S')) then
                                                       createMoves (pos m) (val m) ( inBoard (getSoldierMoves (pos m) dir) mypList) 
                                                       ++ (h fList ms mypList dir)                   
                                                   else if ((val m == 'G') || (val m == 'g')) then
                                                       createMoves (pos m) (val m) ( inPalace (getGeneralMoves (pos m)) mypList) 
                                                       ++ (h fList ms mypList dir)  

                                                  else if ((val m == 'A') || (val m == 'a')) then
                                                       createMoves (pos m) (val m) ( inPalace (getAdvisorMoves (pos m)) mypList) 
                                                       ++ (h fList ms mypList dir)  

                                                  else if (val m == 'E') || (val m == 'e') then
                                                       createMoves (pos m) (val m) ( inArea (getElephantMoves fList (pos m)) mypList dir) 
                                                       ++ (h fList ms mypList dir)  

                                                  else if (val m == 'H') || (val m == 'h') then
                                                       createMoves (pos m) (val m) ( inBoard (getHorseMoves fList (pos m) ) mypList) 
                                                       ++ (h fList ms mypList dir)                                                          

                                                  else if (val m == 'R') || (val m == 'r') then
                                                       createMoves (pos m) (val m) (getRookMoves fList (pos m) dir) 
                                                       ++ (h fList ms mypList dir)
                                                  else 
                                                       createMoves (pos m) (val m) (getCannonMoves fList (pos m) dir) 
                                                       ++ (h fList ms mypList dir)

                                 
isSchach :: [Figure] -> Move -> Int  -> Bool--1 Figurelist 2enemydir 3mymove 4myKing
isSchach figList move dir  = let
                                   newFig = Figure (finalPos move) (name move)
                                   l = [newFig] ++ (filter  (\f -> ( ((val f) /= (val newFig)) && ((pos f) /= (pos newFig)) ) ) figList)
                                   king = pos (getKing l (- dir)) 
                                   oppMoves = preListMoves l dir
                                   finalMoves = map (\m -> finalPos m) oppMoves
                              in
                                   h finalMoves king 
                                   where
                                      h :: [Position] -> Position -> Bool
                                      h [] _ = False
                                      h (p : ps) k = if p == k then True
                                                   else h ps k  

getKing :: [Figure] -> Int -> Figure
getKing (f:fs) dir = if ((val f == 'g') && (dir == 1)) then f
                     else if  ((val f == 'G') && (dir == -1)) then f
                     else getKing fs dir

isTodesblick :: [Figure] -> Move -> Int  -> Bool --1 Figurelist 2mydir 3mymove 4enemyKing
isTodesblick figList move dir = let
                                   newFig = Figure (finalPos move) (name move)
                                   oppKing = pos (getKing figList ( - dir))
                                   l = [newFig] ++ (filter  (\f -> not ((((val f) == (val newFig))) || ((pos f) == (pos newFig))) ) figList) 
                                   x1 = getX oppKing
                                   x2 = getX (pos (getKing l dir))
                                   y1 = min (getY oppKing) (getY (pos (getKing l dir)))
                                   y2 = max (getY oppKing) (getY (pos (getKing l dir)))
                              in
                                   if (
                                        (x1 == x2) && 
                                        (null (filter (\f -> 
                                             if (((getX (pos f)) == x1) && ((getY (pos f)) < y2) && ((getY (pos f)) > y1 ) ) then True 
                                             else False)
                                         l) )) then True 
                                   else False

