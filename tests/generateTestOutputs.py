#!/usr/bin/python3

import os
import shutil

def main():
    if os.path.exists("./outputs"):
        shutil.rmtree("./outputs")
    for project in os.listdir("./testProjects"):
        os.system('java -jar ./../target/skriptlangcompiler-1.0-shaded.jar ./testProjects/'+ project +' ./outputs/' + project + " 1")

if __name__ == "__main__":
    main()
