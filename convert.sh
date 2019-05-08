#!/bin/bash
parallel inkscape -f {} -e pngs/{.} ::: svgOutput/*.svg

mv ./pngs/svgOutput/* ./pngs/
rm -rf ./pngs/svgOutput
