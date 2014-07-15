How to commit/push:
git add <added/modified files>
git rm <deleted files>
git commit -m <message>
git push -u 

OR

git commit -a --message="MESSAGE"
git push -u

==================================================
What is the difference between fetch and pull?
==================================================
There are two ways to get commits from a remote repository or branch: git fetch and git pull. While they might seem similar at first, there are distinct differences you should consider.

Pull
--------------------------------------------------------
$ git pull upstream master
# Pulls commits from 'upstream' and stores them in the local repository
--------------------------------------------------------
When you use git pull, git tries to automatically do your work for you. It is context sensitive, so git will merge any pulled commits into the branch you are currently working in. One thing to keep in mind is that git pull automatically merges the commits without letting you review them first. If you don't closely manage your branches you may run into frequent conflicts.

Fetch & Merge
--------------------------------------------------------
$ git fetch upstream
# Fetches any new commits from the original repository
$ git merge upstream/master
# Merges any fetched commits into your working files
--------------------------------------------------------
When you git fetch, git retrieves any commits from the target remote that you do not have and stores them in your local repository. However, it does not merge them with your current branch. This is particularly useful if you need to keep your repository up to date but are working on something that might break if you update your files. To integrate the commits into your local branch, you use git merge. This combines the specified branches and prompts you if there are any conflicts.

=========================================================
BRANCHES
=========================================================
Branching allows you to build new features or test out ideas without putting your main project at risk. In git, branch is a sort of bookmark that references the last commit made in the branch. This makes branches very small and easy to work with.

How do I use branches?
-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
Branches are pretty easy to work with and will save you a lot of headaches, especially when working with multiple people. To create a branch and begin working in it, run these commands:

--------------------------------------------------------
$ git branch mybranch
# Creates a new branch called "mybranch"
$ git checkout mybranch
# Makes "mybranch" the active branch
--------------------------------------------------------
Alternatively, you can use the shortcut:
--------------------------------------------------------
$ git checkout -b mybranch
# Creates a new branch called "mybranch" and makes it the active branch
--------------------------------------------------------
To switch between branches, use git checkout.
--------------------------------------------------------
$ git checkout master
# Makes "master" the active branch
$ git checkout mybranch
# Makes "mybranch" the active branch
--------------------------------------------------------
Once you're finished working on your branch and are ready to combine it back into the master branch, use merge.
--------------------------------------------------------
$ git checkout master
# Makes "master" the active branch
$ git merge mybranch
# Merges the commits from "mybranch" into "master"
$ git branch -d mybranch
# Deletes the "mybranch" branch
--------------------------------------------------------
Tip: When you switch between branches, the files that you work on (the "working copy") are updated to reflect the changes in the new branch. If you have changes you have not committed, git will ensure you do not lose them. Git is also very careful during merges and pulls to ensure you don't lose any changes. When in doubt, commit early and commit often.
